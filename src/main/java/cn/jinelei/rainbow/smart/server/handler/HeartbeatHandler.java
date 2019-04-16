package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.util.Date;

/**
 * @author jinelei
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HeartbeatHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ConnectionContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())
                && msg instanceof Message.Pkt
                && ((Message.Pkt) msg).getDir()
                && Message.Tag.HEARTBEAT.equals(((Message.Pkt) msg).getTag())) {
            Message.Pkt pkt = (Message.Pkt) msg;
            LOGGER.debug("{}: received: {}", ctx.channel().id(), msg);
            if (ConnectionContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())) {
                ctx.writeAndFlush(getPktHeartbeatRsp(pkt));
            } else if (ConnectionContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
                ConnectionContainer.getInstance().suddenDeathToOnline(ctx.channel().id());
                LOGGER.debug("{}: suddendeath to online", ctx.channel().id());
                ctx.writeAndFlush(getPktHeartbeatRsp(pkt));
            }
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private Message.Pkt getPktHeartbeatRsp(Message.Pkt pkt) {
        return Message.Pkt.newBuilder()
                .setDstAddr(pkt.getSrcAddr())
                .setSrcAddr("0")
                .setDir(false)
                .setSeq(pkt.getSeq() + 1)
                .setTag(pkt.getTag())
                .setTimestamp(new Date().getTime())
                .setHeartbeatRspMsg(
                        Message.HeartbeatRspMsg.newBuilder()
                                .setErrcode(Common.ErrCode.SUCCESS)
                ).build();
    }
}
