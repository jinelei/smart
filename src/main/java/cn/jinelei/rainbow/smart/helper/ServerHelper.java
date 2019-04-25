package cn.jinelei.rainbow.smart.helper;

import cn.jinelei.rainbow.smart.server.coder.RainMessageDecoder;
import cn.jinelei.rainbow.smart.server.coder.RainMessageEncoder;
import cn.jinelei.rainbow.smart.server.handler.DevStatusHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author jinelei
 */
public class ServerHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHelper.class);

    public static ChannelHandler[] init() {
        return Arrays.asList(
                new RainMessageEncoder(),
                new RainMessageDecoder(),
                new DevStatusHandler()
        ).toArray(new ChannelHandler[0]);
    }

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
