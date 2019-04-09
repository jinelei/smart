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

            ConnectionContainer.getInstance().addFetures(ctx.channel().id(), featureList);
            ConnectionContainer.getInstance().addMacAddr(ctx.channel().id(), pkt.getSrcAddr());
            ConnectionContainer.getInstance().addTimeout(ctx.channel().id(), timeout);

            LOGGER.debug("set timeout: {}", timeout);
            ctx.pipeline().addBefore("timeoutHandler", "idleHandler", new IdleStateHandler(timeout, 0, 0, TimeUnit.SECONDS));

            Message.LoginRspMsg rsp = Message.LoginRspMsg.newBuilder()
                    .setErrcode(Common.ErrCode.SUCCESS).build();

//            ctx.fireChannelRead(msg);
            ReferenceCountUtil.release(msg);
//            LOGGER.debug("response: {}", rsp);
            ctx.writeAndFlush(rsp);
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
