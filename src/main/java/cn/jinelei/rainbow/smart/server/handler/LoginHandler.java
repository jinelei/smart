package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Common;
import protobuf.Message;

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

            Message.LoginRspMsg rsp = Message.LoginRspMsg.newBuilder()
                    .setErrcode(Common.ErrCode.SUCCESS).build();

            ReferenceCountUtil.release(msg);
            ctx.writeAndFlush(rsp);
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
