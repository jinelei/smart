package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * @author jinelei
 */
public class TimeoutHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutHandler.class);
    private static final int MAX_WAIT_COUNT = 3;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            switch (((IdleStateEvent) evt).state()) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    if (ConnectionContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())) {
                        IdleStateHandler idleStateHandler = (IdleStateHandler) ctx.pipeline().get(IdleStateHandler.class.getSimpleName());
                        long time = Instant.now().toEpochMilli() - idleStateHandler.getAllIdleTimeInMillis();
                        ConnectionContainer.getInstance().onlineToSuddenDeath(ctx.channel().id(), time);
                        LOGGER.debug("{}: move to suddenDeath", ctx.channel().id());
                    } else if (ConnectionContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
                        int waitCount = ConnectionContainer.getInstance().getWaitCount(ctx.channel().id());
                        if (waitCount > MAX_WAIT_COUNT) {
                            ConnectionContainer.getInstance().suddenDeathToDead(ctx.channel().id());
                            ctx.disconnect();
                            LOGGER.debug("{}: force disconnect", ctx.channel().id());
                        } else {
                            LOGGER.debug("{}: wait {}", ctx.channel().id(), waitCount);
                        }
                    } else {
                        LOGGER.debug("{}: already dead", ctx.channel().id());
                    }
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
