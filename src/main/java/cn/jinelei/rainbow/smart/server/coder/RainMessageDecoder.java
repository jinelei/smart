package cn.jinelei.rainbow.smart.server.coder;

import cn.jinelei.rainbow.smart.helper.ServerHelper;
import cn.jinelei.rainbow.smart.model.L1Bean;
import cn.jinelei.rainbow.smart.server.message.Header;
import cn.jinelei.rainbow.smart.server.message.Payload;
import cn.jinelei.rainbow.smart.server.message.RainMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RainMessageDecoder extends ByteToMessageDecoder {


    private static final Logger LOGGER = LoggerFactory.getLogger(RainMessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            RainMessage.RainMessageBuilder builder = new RainMessage.RainMessageBuilder();
            if (in.readableBytes() >= RainMessage.HEADER_LEN) {
                int beginReader;
                while (true) {
                    beginReader = in.readerIndex();
                    in.markReaderIndex();
                    if (in.readShort() == L1Bean.magic)
                        break;
                    in.resetReaderIndex();
                    in.readByte();
                    if (in.readableBytes() < RainMessage.HEADER_LEN)
                        return;
                }
                byte crc = in.readByte();
                byte version = in.readByte();
                byte type = in.readByte();
                byte key = in.readByte();
                byte sequence = in.readByte();
                long timestamp = in.readLong();
                in.skipBytes(RainMessage.LEN_RESERVED);
                int length = in.readInt();
                builder.withHeader(new Header(crc, version, type, key, sequence, timestamp, length));
                if (in.readableBytes() < length)
                    throw new Exception(String.format("invalid data length: %d, actually length: %d", length, in.readableBytes()));
                int readBytes = 0;
                while (readBytes < length) {
                    // read payload
                    byte payloadType = in.readByte();
                    byte payloadLenth = in.readByte();
                    byte payloadCount = in.readByte();
                    byte[] payloadData = new byte[payloadCount * payloadLenth];
                    in.readBytes(payloadData);
                    Payload payload = new Payload(payloadType, payloadLenth, payloadCount, payloadData);
                    readBytes += payload.getLength();
                    builder.withPayload(payload);
                }
                RainMessage message = builder.build();
                LOGGER.info("({}) decode: {}", ctx.channel().id(), message);
                out.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ServerHelper.disconnect(ctx);
        }
    }

}