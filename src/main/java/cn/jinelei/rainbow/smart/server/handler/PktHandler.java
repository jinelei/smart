package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * @author jinelei
 */
public class PktHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PktHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ConnectionContainer.getInstance().preLogin(ctx.channel().id(), ctx.channel());
        LOGGER.debug("{}: registered", ctx.channel().id());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果Channel已死，则手动关闭
        if (ConnectionContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
            LOGGER.error("connect heartbeat timeout");
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
        LOGGER.debug("{}: unregistered", ctx.channel().id());
        super.channelRegistered(ctx);
    }
}
