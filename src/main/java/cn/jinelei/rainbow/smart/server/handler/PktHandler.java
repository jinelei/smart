package cn.jinelei.rainbow.smart.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jinelei
 */
public class PktHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PktHandler.class);
    ThreadLocal<Map<ChannelId, Channel>> onlineMac = new ThreadLocal<Map<ChannelId, Channel>>() {
        @Override
        protected Map<ChannelId, Channel> initialValue() {
            return new LinkedHashMap<>();
        }
    };

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        onlineMac.get().put(ctx.channel().id(), ctx.channel());
        LOGGER.debug(onlineMac.get().toString());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        onlineMac.get().remove(ctx.channel().id());
        LOGGER.debug(onlineMac.get().toString());
    }
}
