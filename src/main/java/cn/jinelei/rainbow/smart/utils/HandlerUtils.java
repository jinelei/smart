package cn.jinelei.rainbow.smart.utils;

import io.netty.handler.codec.protobuf.ProtobufDecoder;
import protobuf.Message;

import java.util.Arrays;

/**
 * @author jinelei
 */
public class HandlerUtils {
    public static ProtobufDecoder[] initDecoders() {
        return Arrays.asList(
                new ProtobufDecoder(Message.Pkt.getDefaultInstance())
        ).toArray(new ProtobufDecoder[0]);
    }
}
