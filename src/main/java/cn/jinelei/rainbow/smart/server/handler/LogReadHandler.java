package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.utils.HexUtils;
import com.googlecode.protobuf.format.JsonFormat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import protobuf.Message;

/**
 * @author jinelei
 */
public class LogReadHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(LogReadHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.debug("{}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message.Pkt) {
            LOGGER.debug("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(), JsonFormat.printToString((com.google.protobuf.Message) msg));
            LOGGER.info("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(), HexUtils.toHexString(((Message.Pkt) msg).toByteArray()));
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
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.debug("{}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.debug("{}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.debug("{}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id());
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.debug("{}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id());
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOGGER.debug("{}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id());
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("{}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id());
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.debug("{}: {} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(), cause.getMessage());
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

}
