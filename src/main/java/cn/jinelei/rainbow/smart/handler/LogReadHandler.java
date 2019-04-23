package cn.jinelei.rainbow.smart.handler;

import cn.jinelei.rainbow.smart.exception.DecoderException;
import cn.jinelei.rainbow.smart.model.L1Bean;
import cn.jinelei.rainbow.smart.container.ConnContainer;
import cn.jinelei.rainbow.smart.helper.HexHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Map;

/**
 * @author jinelei
 */
public class LogReadHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(LogReadHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // LOGGER.debug("{}: {}",
        // Thread.currentThread().getStackTrace()[1].getMethodName(),
        // ctx.channel().id());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof L1Bean) {
            LOGGER.debug("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(),
                    ((L1Bean) msg).toString());
            LOGGER.info("{}: {}: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(),
                    HexHelper.toHexString(((L1Bean) msg).toBytes()));
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
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // LOGGER.debug("{}: {}",
        // Thread.currentThread().getStackTrace()[1].getMethodName(),
        // ctx.channel().id());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // LOGGER.debug("{}: {}",
        // Thread.currentThread().getStackTrace()[1].getMethodName(),
        // ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // LOGGER.debug("{}: {}",
        // Thread.currentThread().getStackTrace()[1].getMethodName(),
        // ctx.channel().id());
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // LOGGER.debug("{}: {}",
        // Thread.currentThread().getStackTrace()[1].getMethodName(),
        // ctx.channel().id());
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
        LOGGER.error("{}: {} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ctx.channel().id(),
                cause.getMessage());
        if (cause instanceof DecoderException) {
            Map<String, Object> info = ConnContainer.getInfoById(ctx.channel().id());
            Object tmp = info.getOrDefault(ConnContainer.KEY_MAC, null);
            if (tmp == null) {
                LOGGER.info("channel id not found info: " + ctx.channel().id());
            } else if (tmp instanceof String) {
                String mac = (String) tmp;
//                L1Bean rsp = new L1Bean.L1BeanBuilder()
//                        .withSrcAddr(Constants.SERVER_ADDR_BYTES)
//                        .withDstAddr(PktHelper.macStringToBytes(mac))
//                        .withVersion(Constants.Default.DEFAULT_VERSION)
            } else {
                LOGGER.error("channel id mac address is not string: " + tmp);
            }
        } else {
            cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }
    }

}
