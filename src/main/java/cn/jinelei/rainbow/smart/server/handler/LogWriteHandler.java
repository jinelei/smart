package cn.jinelei.rainbow.smart.server.handler;

import com.googlecode.protobuf.format.JsonFormat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import protobuf.Message;

/**
 * @author jinelei
 */
public class LogWriteHandler extends ChannelOutboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(LogWriteHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Message.Pkt) {
            LOGGER.debug("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(), JsonFormat.printToString((com.google.protobuf.Message) msg));
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
            LOGGER.debug("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(), sb.toString());
        }
        super.write(ctx, msg, promise);
    }
}
