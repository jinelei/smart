package cn.jinelei.rainbow.smart.server.netty.handler;

import cn.jinelei.rainbow.smart.protobuf.MessageProto;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

import java.util.Arrays;

/**
 * @author jinelei
 */
public class HandlerUtils {
    public static ProtobufDecoder[] initDecoders() {
        return Arrays.asList(
                new ProtobufDecoder(MessageProto.HeartBeat.getDefaultInstance())
        ).toArray(new ProtobufDecoder[0]);
    }
}
