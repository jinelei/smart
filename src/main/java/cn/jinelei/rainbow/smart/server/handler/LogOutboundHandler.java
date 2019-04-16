package cn.jinelei.rainbow.smart.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * @author jinelei
 */
public class LogOutboundHandler extends ChannelOutboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(LogOutboundHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        ((ByteBuf) msg).copy().forEachByteDesc(value -> {
            if (sb.length() > 1)
                sb.append(" ");
            sb.append(String.format("%02X", value));
            return true;
        });
        sb.append("]");
        LOGGER.debug("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(), sb.toString());
        super.write(ctx, msg, promise);
    }
}
