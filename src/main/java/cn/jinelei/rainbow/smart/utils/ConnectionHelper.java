package cn.jinelei.rainbow.smart.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ConnectionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionHelper.class);

    public static void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
        LOGGER.debug("disconnect channel({})", ctx.channel().id());
        if (promise != null)
            ctx.disconnect(promise);
    }

    public static void disconnect(ChannelHandlerContext ctx) {
        LOGGER.debug("disconnect channel({})", ctx.channel().id());
        ctx.disconnect();
    }

}