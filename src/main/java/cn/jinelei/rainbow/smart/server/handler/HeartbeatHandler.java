package cn.jinelei.rainbow.smart.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.util.Date;

/**
 * @author jinelei
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message.Pkt
                && ((Message.Pkt) msg).getDir()
                && Message.Tag.HEARTBEAT.equals(((Message.Pkt) msg).getTag())) {
            Message.Pkt pkt = (Message.Pkt) msg;
            LOGGER.debug("{}: received: {}", ctx.channel().id(), msg);
            Message.Pkt rsp = Message.Pkt.newBuilder()
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
            ReferenceCountUtil.release(msg);
            ctx.writeAndFlush(rsp);
        } else {
            LOGGER.debug("other: received: {}", msg);
            super.channelRead(ctx, msg);
        }
    }
}
