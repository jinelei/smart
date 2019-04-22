package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.model.JySmartProto;
import cn.jinelei.rainbow.smart.utils.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * @author jinelei
 */
public class LogWriteHandler extends ChannelOutboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(LogWriteHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof JySmartProto) {
            LOGGER.debug("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(),
                    ((JySmartProto) msg).toString());
            LOGGER.info("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(),
                    HexUtils.toHexString(((JySmartProto) msg).getBytes()));
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            ((ByteBuf) msg).copy().forEachByteDesc(value -> {
                if (sb.length() > 1) {
                    sb.append(" ");
                }
                sb.append(String.format("%02X", value));
                return true;
            });
            sb.append("]");
            LOGGER.debug("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(),
                    sb.toString());
        }
        super.write(ctx, msg, promise);
    }
}
