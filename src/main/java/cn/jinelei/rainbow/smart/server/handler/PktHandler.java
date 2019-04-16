package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author jinelei
 */
public class PktHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(PktHandler.class);
    private static final int TIMEOUT = 10;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ConnectionContainer.getInstance().preLogin(ctx.channel().id(), ctx.channel());
        super.channelRegistered(ctx);
        LOGGER.debug("{}: set default TIMEOUT: {}", ctx.channel().id(), TIMEOUT);
        ctx.pipeline().addBefore(TimeoutHandler.class.getSimpleName() + "#0",
                IdleStateHandler.class.getSimpleName(),
                new IdleStateHandler(TIMEOUT, 0, 0, TimeUnit.SECONDS));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果Channel已死，则手动关闭
        if (ConnectionContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
            LOGGER.error("connect heartbeat TIMEOUT");
            ctx.disconnect().addListener(future -> ConnectionContainer.getInstance().getDeadMap().remove(ctx.channel().id()));
        }
        // 如果是Channel濒死，则移动Channel
        if (ConnectionContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
            LOGGER.debug("restore channel online");
            ConnectionContainer.getInstance().suddenDeathToOnline(ctx.channel().id());
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ConnectionContainer.getInstance().onlineToDead(ctx.channel().id(), Instant.now().toEpochMilli());
        super.channelRegistered(ctx);
        LOGGER.debug("{}: remove default TIMEOUT: {}", ctx.channel().id(), TIMEOUT);
        ctx.pipeline().remove(IdleStateHandler.class);
    }
}
