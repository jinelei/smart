package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                case ALL_IDLE:
                case READER_IDLE:
                case WRITER_IDLE:
                    LOGGER.error("timeout id: {}", ctx.channel().id());
                    if (ConnectionContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())) {
                        ConnectionContainer.getInstance().onlineToSuddenDeath(ctx.channel().id());
                    } else if (ConnectionContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id())) {
                        int waitCount = ConnectionContainer.getInstance().getWaitCount(ctx.channel().id());
                        if (waitCount > MAX_WAIT_COUNT) {
                            ConnectionContainer.getInstance().suddenDeathToDead(ctx.channel().id());
                            ctx.disconnect();
                            LOGGER.debug("force disconnect id: {}", ctx.channel().id());
                        } else {
                            LOGGER.debug("wait {} id: {}", waitCount, ctx.channel().id());
                        }
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
