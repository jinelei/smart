package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinelei
 */
public class PktHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PktHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        ConnectionContainer.getInstance().addChannel(ctx.channel().id(), ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        ConnectionContainer.getInstance().onlineToSuddenDeath(ctx.channel().id());
    }
}
