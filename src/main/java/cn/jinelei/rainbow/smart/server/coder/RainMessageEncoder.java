package cn.jinelei.rainbow.smart.server.coder;

import cn.jinelei.rainbow.smart.helper.ServerHelper;
import cn.jinelei.rainbow.smart.server.message.RainMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RainMessageEncoder extends MessageToByteEncoder<RainMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RainMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, RainMessage msg, ByteBuf out) throws Exception {
        try {
            LOGGER.info("({}) encode: {}", ctx.channel().id(), msg);
            out.writeBytes(msg.toBytes());
        } catch (Exception e) {
            e.printStackTrace();
            ServerHelper.disconnect(ctx);
        }
    }

}