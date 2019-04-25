package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.container.ConnContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author jinelei
 */
public class TimeoutHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutHandler.class);
    private static final int MAX_WAIT_COUNT = 3;
    private static final int TIMEOUT = 10;


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ConnContainer.getInstance().preLogin(ctx.channel().id(), ctx.channel());
        super.channelRegistered(ctx);
        LOGGER.debug("{}: set default TIMEOUT: {}", ctx.channel().id(), TIMEOUT);
        ctx.pipeline().addBefore(TimeoutHandler.class.getSimpleName() + "#0",
                IdleStateHandler.class.getSimpleName(),
                new IdleStateHandler(TIMEOUT, 0, 0, TimeUnit.SECONDS));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果Channel已死，则手动关闭
        if (ConnContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
            LOGGER.error("connect heartbeat TIMEOUT");
            ctx.disconnect().addListener(future -> ConnContainer.getInstance().getDeadMap().remove(ctx.channel().id()));
        }
        // 如果是Channel濒死，则移动Channel
        if (ConnContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
            LOGGER.debug("restore channel online");
            ConnContainer.getInstance().suddenDeathToOnline(ctx.channel().id());
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ConnContainer.getInstance().onlineToDead(ctx.channel().id(), Instant.now().toEpochMilli());
        super.channelRegistered(ctx);
        LOGGER.debug("{}: remove default TIMEOUT: {}", ctx.channel().id(), TIMEOUT);
        ctx.pipeline().remove(IdleStateHandler.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            switch (((IdleStateEvent) evt).state()) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    if (ConnContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
                        // channel first connected server
                        // disconnect this channel
                        ConnContainer.getInstance().getTmpMap().remove(ctx.channel().id());
                        ctx.disconnect(new DefaultChannelPromise(ctx.channel()) {
                            @Override
                            public boolean isDone() {
                                ConnContainer.getInstance().getTmpMap().remove(ctx.channel().id());
                                return super.isDone();
                            }
                        });
                    } else if (ConnContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())) {
                        IdleStateHandler idleStateHandler = (IdleStateHandler) ctx.pipeline().get(IdleStateHandler.class.getSimpleName());
                        long time = Instant.now().toEpochMilli() - idleStateHandler.getAllIdleTimeInMillis();
                        ConnContainer.getInstance().onlineToSuddenDeath(ctx.channel().id(), time);
                        LOGGER.debug("{}: move to suddenDeath", ctx.channel().id());
                    } else if (ConnContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
                        int waitCount = ConnContainer.getInstance().getWaitCount(ctx.channel().id());
                        if (waitCount > MAX_WAIT_COUNT) {
                            ConnContainer.getInstance().suddenDeathToDead(ctx.channel().id());
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
