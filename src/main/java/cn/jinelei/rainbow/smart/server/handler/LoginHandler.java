package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jinelei
 */
public class LoginHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message.Pkt
                && ((Message.Pkt) msg).getDir()
                && Message.Tag.LOGIN.equals(((Message.Pkt) msg).getTag())
                && ((Message.Pkt) msg).hasLoginReqMsg()
        ) {
            Message.Pkt pkt = (Message.Pkt) msg;

            Message.LoginReqMsg req = pkt.getLoginReqMsg();
            List<Common.DevFeature> featureList = req.getDevFeturesList();
            int timeout = req.getTimeout();


            ConnectionContainer.getInstance().login(ctx.channel().id(), featureList, pkt.getSrcAddr(), timeout);

            LOGGER.debug("{}: set timeout: {}", ctx.channel().id(), timeout);
            ctx.pipeline().addBefore(TimeoutHandler.class.getSimpleName() + "#0",
                    IdleStateHandler.class.getSimpleName(),
                    new IdleStateHandler(timeout, 0, 0, TimeUnit.SECONDS));

            Message.Pkt rsp = Message.Pkt.newBuilder()
                    .setSrcAddr(pkt.getDstAddr())
                    .setDstAddr(pkt.getSrcAddr())
                    .setTag(pkt.getTag())
                    .setDir(!pkt.getDir())
                    .setTimestamp(Instant.now().toEpochMilli())
                    .setSeq(pkt.getSeq() + 1)
                    .setLoginRspMsg(Message.LoginRspMsg.newBuilder().setErrcode(Common.ErrCode.SUCCESS).build())
                    .build();

            ctx.writeAndFlush(rsp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

}
