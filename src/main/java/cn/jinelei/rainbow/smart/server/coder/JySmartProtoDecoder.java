package cn.jinelei.rainbow.smart.server.coder;

import java.util.List;

import cn.jinelei.rainbow.smart.model.JySmartProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class JySmartProtoDecoder extends ByteToMessageDecoder {

    public final int MIN_PACKAGE_LENGTH = 31;
    public final int MAX_PACKAGE_LENGTH = 2048;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() >= MIN_PACKAGE_LENGTH){
            if(in.readableBytes() > MAX_PACKAGE_LENGTH){
                in.skipBytes(in.readableBytes());
            }
            int beginReader;
            while(true){
                beginReader = in.readerIndex();
                in.markReaderIndex();
                if(in.readShort() == JySmartProto.magic)
                    break;
                in.resetReaderIndex();
                in.readByte();
                if(in.readableBytes() < MIN_PACKAGE_LENGTH)
                    return;
            }
            byte version = in.readByte();
            byte crc = in.readByte();
            byte[] srcAddr = new byte[6];
            in.readBytes(srcAddr);
            byte[] dstAddr = new byte[6];
            in.readBytes(dstAddr);
            long timestamp = in.readLong();
            byte seq = in.readByte();
            byte category = in.readByte();
            byte tag = in.readByte();
            short last = in.readShort();
            short length = in.readShort();
            byte[] data = new byte[length];
            in.readBytes(data);
            JySmartProto jySmartProto = new JySmartProto(version, crc, srcAddr, dstAddr, timestamp, seq, category, tag, last, length, data);
            out.add(jySmartProto);
        }
    }

}