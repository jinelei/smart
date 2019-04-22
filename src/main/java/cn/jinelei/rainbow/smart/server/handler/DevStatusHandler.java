package cn.jinelei.rainbow.smart.server.handler;

import java.util.ArrayList;
import java.util.List;

import cn.jinelei.rainbow.smart.model.L1Bean;
import cn.jinelei.rainbow.smart.model.enums.Category;
import cn.jinelei.rainbow.smart.model.enums.DevFeature;
import cn.jinelei.rainbow.smart.model.enums.DevStatusTag;
import cn.jinelei.rainbow.smart.model.enums.EnumHelper;
import cn.jinelei.rainbow.smart.model.enums.Strings;
import cn.jinelei.rainbow.smart.server.container.ConnContainer;
import cn.jinelei.rainbow.smart.utils.ConnectionHelper;
import cn.jinelei.rainbow.smart.utils.Endian;
import cn.jinelei.rainbow.smart.utils.ProtoHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * @author jinelei
 */
public class DevStatusHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(DevStatusHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof L1Bean
                && EnumHelper.equals(Strings.SERVER_ADDR, ((L1Bean) msg).getDstAddrString())
                && EnumHelper.equals(Category.DEV_STATUS, ((L1Bean) msg).getCategory())) {
            L1Bean req = (L1Bean) msg;
            L1Bean rsp = ProtoHelper.genRspFromReq(req);
            if (req.getTag() == DevStatusTag.DEV_LOGIN.getValue()) {
                // 收到登录请求
                if (ConnContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
                    // 如果该连接已经存在于临时连接中
                    if (req.getLength() != 9) {
                        LOGGER.error(
                                "login request data must include features(8byte) and timeout(byte): invalid data length: {}",
                                req.getLength());
                        byte[] data = 
                    } else {
                        long features = Endian.Big.toLong(rsp.getData());
                        int timeout = Endian.Big.toUnsignedByte(rsp.getData(), 8);
                        ConnContainer.login(ctx.channel().id(), resolveFeature(features), req.getSrcAddrrString(),
                                timeout);
                    }
                } else {
                    // 如果该连接不存在于临时连接中，则说明该连接已经超时，关闭该连接
                    // todo 关闭该连接
                    ConnectionHelper.disconnect(ctx);
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
                    ctx.writeAndFlush(getPktHeartbeatRsp(req));
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
            ctx.fireChannelRead(msg);
        }
    }

    public List<String> resolveFeature(long features) {
        List<String> featureList = new ArrayList<String>();
        long tmp = 1L;
        while (tmp <= Long.MAX_VALUE) {
            DevFeature feature = EnumHelper.valueOf(DevFeature.RESERVED, features & tmp);
            if (feature != null)
                featureList.add(feature.toString());
        }
        return featureList;
    }
}
