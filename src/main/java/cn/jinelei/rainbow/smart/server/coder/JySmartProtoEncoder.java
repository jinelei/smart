package cn.jinelei.rainbow.smart.server.coder;

import cn.jinelei.rainbow.smart.model.JySmartProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JySmartProtoEncoder extends MessageToByteEncoder<JySmartProto> {

    @Override
    protected void encode(ChannelHandlerContext ctx, JySmartProto msg, ByteBuf out) throws Exception {
        out.writeShort(JySmartProto.magic);
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