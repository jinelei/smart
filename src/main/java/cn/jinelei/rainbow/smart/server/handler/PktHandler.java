package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * @author jinelei
 */
public class PktHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PktHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ConnectionContainer.getInstance().preLogin(ctx.channel().id(), ctx.channel());
        LOGGER.debug("{}: registered", ctx.channel().id());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ConnectionContainer.getInstance().onlineToDead(ctx.channel().id(), Instant.now().toEpochMilli());
        LOGGER.debug("{}: unregistered", ctx.channel().id());
        super.channelRegistered(ctx);
    }
}
