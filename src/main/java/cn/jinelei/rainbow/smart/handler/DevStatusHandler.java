package cn.jinelei.rainbow.smart.handler;

import cn.jinelei.rainbow.smart.container.ConnContainer;
import cn.jinelei.rainbow.smart.helper.Endian;
import cn.jinelei.rainbow.smart.helper.PktHelper;
import cn.jinelei.rainbow.smart.helper.ServerHelper;
import cn.jinelei.rainbow.smart.model.L1Bean;
import cn.jinelei.rainbow.smart.model.enums.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jinelei
 */
public class DevStatusHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(DevStatusHandler.class);
    public static final String UNKNOWN_DEST_ADDRESS = "unknown dest address";
    public static final String LOGIN_SUCCESS = "login success";
    public static final String LOGIN_REQUEST_MUST_INCLUDE_FEATURES_8_BYTE_AND_TIMEOUT_1_BYTE = "login request must include features(8byte) and timeout(1byte)";
    public static final String THIS_CHANNEL_NOT_EXIST_IN_TMP_MAP = "this channel not exist in tmp map";
    public static final String THIS_CHANNEL_NOT_LOGIN = "this channel not login";
    public static final String LOGOUT_SUCCESS = "logout success";
    public static final String CHANNEL_ALREADY_DEAD = "channel already dead";
    public static final String KNOWN_CHANNEL = "known channel";
    public static final String CHANNEL_ALREADY_RESURRECTION = "channel already resurrection";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof L1Bean
                && Constants.SERVER_ADDR_String.equals(PktHelper.macBytesToString(((L1Bean) msg).getDstAddr()))
                && PktHelper.enumEquals(Category.DEV_STATUS, ((L1Bean) msg).getCategory())) {
            L1Bean req = (L1Bean) msg;
            L1Bean.L1BeanBuilder rspBuilder = PktHelper.genRspFromReqBuilder(req);
            if (req.getTag() == DevStatusTag.DEV_LOGIN.getValue()) {
                // 收到登录请求
                if (ConnContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
                    // 如果该连接已经存在于临时连接中
                    if (req.getLength() != 9) {
                        LOGGER.error(LOGIN_REQUEST_MUST_INCLUDE_FEATURES_8_BYTE_AND_TIMEOUT_1_BYTE
                                + " invalid data length: " + req.getLength());
                        ctx.writeAndFlush(rspBuilder.withData(ErrCode.INVALID_PARAM, LOGIN_REQUEST_MUST_INCLUDE_FEATURES_8_BYTE_AND_TIMEOUT_1_BYTE).build());
                    } else {
                        long features = Endian.Big.toLong(req.getData());
                        int timeout = Endian.Big.toUnsignedByte(req.getData(), 8);
                        List<String> featureList = resolveFeature(features);
                        ConnContainer.login(ctx.channel().id(), featureList, PktHelper.macBytesToString(req.getSrcAddr()),
                                timeout);
                        ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS, LOGIN_SUCCESS).build());
                    }
                } else {
                    // 如果该连接不存在于临时连接中，则说明该连接已经超时，关闭该连接
                    LOGGER.error(THIS_CHANNEL_NOT_EXIST_IN_TMP_MAP + " channel id: " + ctx.channel().id());
                    String mac = ConnContainer.getChannelMac(ctx.channel().id());
                    if (mac != null) {
                        rspBuilder.withData(ErrCode.SUCCESS, THIS_CHANNEL_NOT_EXIST_IN_TMP_MAP);
                        ctx.writeAndFlush(rspBuilder.build());
                    }
                    ReferenceCountUtil.release(msg);
                    ServerHelper.disconnect(ctx);
                }
            } else if (req.getTag() == DevStatusTag.DEV_LOGOUT.getValue()) {
                // received logout request: clear login status and disconnect channel
                // not allow not login channel logout
                if (ConnContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
                    LOGGER.error(THIS_CHANNEL_NOT_LOGIN + " channel id: " + ctx.channel().id());
                    ctx.writeAndFlush(rspBuilder.withData(ErrCode.OFFLINE, THIS_CHANNEL_NOT_LOGIN).build());
                } else if (ConnContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())) {
                    ConnContainer.onlineToDead(ctx.channel().id(), Instant.now().toEpochMilli());
                    ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS, LOGOUT_SUCCESS).build());
                } else if (ConnContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
                    ConnContainer.suddenDeathToDead(ctx.channel().id());
                    ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS, LOGOUT_SUCCESS).build());
                } else if (ConnContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
                    LOGGER.error(CHANNEL_ALREADY_DEAD + " channel id: " + ctx.channel().id());
                    ServerHelper.disconnect(ctx);
                }
                ReferenceCountUtil.release(msg);
            } else if (req.getTag() == DevStatusTag.DEV_HEARTBEAT.getValue()) {
                // 收到连接的心跳请求
                if (ConnContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())) {
                    // 如果该连接已经在线，可以重新协商心跳时间
                    // todo: 重新协调心跳时间
                    // ctx.writeAndFlush(getPktHeartbeatRsp(pkt));
                } else if (ConnContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
                    // 如果该连接已经进入濒死队列，则将连接移到在线队列中
                    // todo: 重新协调心跳时间
                    ConnContainer.getInstance().suddenDeathToOnline(ctx.channel().id());
                    LOGGER.debug("{}: suddendeath to online", ctx.channel().id());
                    ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS, CHANNEL_ALREADY_RESURRECTION).build());
                } else if (ConnContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
                    // 如果该队列已经进入死亡队列，则返回心跳失败
                    LOGGER.error(CHANNEL_ALREADY_DEAD + " channel id: " + ctx.channel().id());
                    ServerHelper.disconnect(ctx);
                } else {
                    // 该连接不在认识的连接中，丢弃该连接
                    LOGGER.error(KNOWN_CHANNEL + " channel id: " + ctx.channel().id());
                    ServerHelper.disconnect(ctx);
                }
            } else {
                // 不支持的Tag
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    public List<String> resolveFeature(long features) {
        List<String> featureList = new ArrayList<String>();
        long tmp = 1L;
        long max_feature = 1L << 62;
        while (tmp < max_feature) {
            DevFeature feature = DevFeature.valueOf(features & tmp);
            if (feature != null && feature != DevFeature.RESERVED)
                featureList.add(feature.toString());
            tmp = tmp << 1L;
        }
        return featureList;
    }
}
