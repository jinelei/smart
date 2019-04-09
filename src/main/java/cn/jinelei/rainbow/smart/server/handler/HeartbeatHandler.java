package cn.jinelei.rainbow.smart.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import protobuf.Message;

/**
 * @author jinelei
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message.Pkt
                && Message.Tag.HEARTBEAT.equals(((Message.Pkt) msg).getTag())) {
            Message.Pkt pkt = (Message.Pkt) msg;
            System.out.println(ctx.channel().id());
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
