package cn.jinelei.rainbow.smart.helper;

import cn.jinelei.rainbow.smart.server.coder.L1BeanDecoder;
import cn.jinelei.rainbow.smart.server.coder.L1BeanEncoder;
import cn.jinelei.rainbow.smart.server.handler.LogReadHandler;
import cn.jinelei.rainbow.smart.server.handler.LogWriteHandler;
import cn.jinelei.rainbow.smart.server.handler.TimeoutHandler;
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
                new TimeoutHandler(),
                new L1BeanEncoder(),
                new L1BeanDecoder(),
                new LogReadHandler(),
                new LogWriteHandler()
                // new HeartbeatHandler(),
                // new LoginHandler(),
                // new DevStatusHandler()
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
