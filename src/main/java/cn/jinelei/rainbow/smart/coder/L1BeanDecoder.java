package cn.jinelei.rainbow.smart.coder;

import cn.jinelei.rainbow.smart.model.L1Bean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class L1BeanDecoder extends ByteToMessageDecoder {

    public final int MIN_PACKAGE_LENGTH = 31;
    public final int MAX_PACKAGE_LENGTH = 2048;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= MIN_PACKAGE_LENGTH) {
            if (in.readableBytes() > MAX_PACKAGE_LENGTH) {
                in.skipBytes(in.readableBytes());
            }
            int beginReader;
            while (true) {
                beginReader = in.readerIndex();
                in.markReaderIndex();
                if (in.readShort() == L1Bean.magic)
                    break;
                in.resetReaderIndex();
                in.readByte();
                if (in.readableBytes() < MIN_PACKAGE_LENGTH)
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
            if (in.readableBytes() < length)
                throw new Exception(String.format("invalid data length: %d, actually length: %d", length, in.readableBytes()));
            in.readBytes(data);
            L1Bean l1Bean = new L1Bean.L1BeanBuilder()
                    .withVersion(version)
                    .withCrc(crc)
                    .withSrcAddr(srcAddr)
                    .withDstAddr(dstAddr)
                    .withTimestamp(timestamp)
                    .withSeq(seq)
                    .withCategory(category)
                    .withTag(tag)
                    .withLast(last)
                    .withData(data)
                    .build();
            out.add(l1Bean);
        }
    }

}