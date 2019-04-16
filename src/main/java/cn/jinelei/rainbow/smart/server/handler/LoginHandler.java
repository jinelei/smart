package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jinelei
 */
public class LoginHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(LoginHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof Message.Pkt
                && ((Message.Pkt) msg).getDir()
                && Message.Tag.LOGIN.equals(((Message.Pkt) msg).getTag())
                && ((Message.Pkt) msg).hasLoginReqMsg()
        ) {
            Message.Pkt pkt = (Message.Pkt) msg;

            Message.Pkt.Builder rspBuilder = Message.Pkt.newBuilder();
            rspBuilder.setSrcAddr(pkt.getDstAddr())
                    .setDstAddr(pkt.getSrcAddr())
                    .setTag(pkt.getTag())
                    .setDir(!pkt.getDir())
                    .setTimestamp(Instant.now().toEpochMilli())
                    .setSeq(pkt.getSeq() + 1);

            if (ConnectionContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
                // first message after build connection
                // remove idle handler
                ctx.pipeline().remove(IdleStateHandler.class);

                Message.LoginReqMsg req = pkt.getLoginReqMsg();
                List<Common.DevFeature> featureList = req.getDevFeturesList();
                int timeout = req.getTimeout();
                ConnectionContainer.getInstance().login(ctx.channel().id(), featureList, pkt.getSrcAddr(), timeout);

                LOGGER.debug("{}: reset timeout: {}", ctx.channel().id(), timeout);
                ctx.pipeline().addBefore(TimeoutHandler.class.getSimpleName() + "#0",
                        IdleStateHandler.class.getSimpleName(),
                        new IdleStateHandler(timeout, 0, 0, TimeUnit.SECONDS));

                rspBuilder.setLoginRspMsg(Message.LoginRspMsg.newBuilder().setErrcode(Common.ErrCode.SUCCESS).build());
            } else {
                rspBuilder.setLoginRspMsg(Message.LoginRspMsg.newBuilder().setErrcode(Common.ErrCode.FAILURE).build());
            }
            ctx.writeAndFlush(rspBuilder.build());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

}
