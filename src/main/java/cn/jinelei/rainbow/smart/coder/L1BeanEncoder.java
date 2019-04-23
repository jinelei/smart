package cn.jinelei.rainbow.smart.coder;

import cn.jinelei.rainbow.smart.model.L1Bean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class L1BeanEncoder extends MessageToByteEncoder<L1Bean> {

    @Override
    protected void encode(ChannelHandlerContext ctx, L1Bean msg, ByteBuf out) throws Exception {
        out.writeShort(L1Bean.magic);
        out.writeByte(msg.getVersion());
        out.writeByte(msg.getCrc());
        out.writeBytes(msg.getSrcAddr());
        out.writeBytes(msg.getDstAddr());
        out.writeLong(msg.getTimestamp());
        out.writeByte(msg.getSeq());
        out.writeByte(msg.getCategory());
        out.writeByte(msg.getTag());
        out.writeShort(msg.getLast());
        out.writeShort(msg.getLength());
        out.writeBytes(msg.getData());
    }

}