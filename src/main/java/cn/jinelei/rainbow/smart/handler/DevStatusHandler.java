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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jinelei
 */
public class DevStatusHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(DevStatusHandler.class);
    public static final String UNKNOWN_DEST_ADDRESS = "unknown dest address";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof L1Bean
                && Constants.SERVER_ADDR_String.equals(PktHelper.macBytesToString(((L1Bean) msg).getDstAddr()))
                && PktHelper.enumEquals(Category.DEV_STATUS, ((L1Bean) msg).getCategory())) {
            L1Bean req = (L1Bean) msg;
            L1Bean rsp = PktHelper.genRspFromReq(req);
            if (req.getTag() == DevStatusTag.DEV_LOGIN.getValue()) {
                // 收到登录请求
                if (ConnContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
                    // 如果该连接已经存在于临时连接中
                    if (req.getLength() != 9) {
                        LOGGER.error(
                                "login request data must include features(8byte) and timeout(byte): invalid data length: {}",
                                req.getLength());
                    } else {
                        long features = Endian.Big.toLong(req.getData());
                        int timeout = Endian.Big.toUnsignedByte(req.getData(), 8);
                        List<String> featureList = resolveFeature(features);
                        ConnContainer.login(ctx.channel().id(), featureList, PktHelper.macBytesToString(req.getSrcAddr()),
                                timeout);
                    }
                } else {
                    // 如果该连接不存在于临时连接中，则说明该连接已经超时，关闭该连接
                    // todo 关闭该连接
                    ServerHelper.disconnect(ctx);
                }
            } else if (req.getTag() == DevStatusTag.DEV_LOGOUT.getValue()) {

            } else if (req.getTag() == DevStatusTag.DEV_HEARTBEAT.getValue()) {
                // 收到连接的心跳请求
                if (ConnContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())) {
                    // 如果该连接已经在线，可以重新协商心跳时间
                    // todo: 重新协调心跳时间
                    // ctx.writeAndFlush(getPktHeartbeatRsp(pkt));
                } else if (ConnContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
                    // 如果该连接已经进入濒死队列，则将连接移到在线队列中
                    ConnContainer.getInstance().suddenDeathToOnline(ctx.channel().id());
                    LOGGER.debug("{}: suddendeath to online", ctx.channel().id());
//                    ctx.writeAndFlush(getPktHeartbeatRsp(req));
                } else if (ConnContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
                    // 如果该队列已经进入死亡队列，则返回心跳失败
                } else {
                    // 该连接不在认识的连接中，丢弃该连接
                }
            } else {
                // 不支持的Tag

            }
            ReferenceCountUtil.release(msg);
        } else {
            String mac = ConnContainer.getChannelMac(ctx.channel().id());
            if (mac != null) {
                L1Bean.L1BeanBuilder builder = new L1Bean.L1BeanBuilder()
                        .withMagic(L1Bean.magic)
                        .withCrc(0)
                        .withVersion(Constants.Default.DEFAULT_VERSION)
                        .withSrcAddr(Constants.SERVER_ADDR_BYTES)
                        .withDstAddr(PktHelper.macStringToBytes(mac))
                        .withTimestamp(new Date().getTime())
                        .withLast(0)
                        .withSeq(1)
                        .withReserved(0)
                        .withData(ErrCode.SUCCESS, UNKNOWN_DEST_ADDRESS);
                ctx.writeAndFlush(builder.build());
            }
            ReferenceCountUtil.release(msg);
            ServerHelper.disconnect(ctx);
        }
    }

    public List<String> resolveFeature(long features) {
        List<String> featureList = new ArrayList<String>();
        long tmp = 1L;
        while (tmp <= Long.MAX_VALUE) {
            DevFeature feature = DevFeature.valueOf(features & tmp);
            if (feature != null)
                featureList.add(feature.toString());
            tmp <<= 1;
        }
        return featureList;
    }
}
