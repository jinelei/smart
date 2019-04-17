package cn.jinelei.rainbow.smart.utils;

import cn.jinelei.rainbow.smart.server.handler.*;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import protobuf.Message;

import java.util.Arrays;

/**
 * @author jinelei
 */
public class HandlerUtils {

    public static ChannelHandler[] init() {
        return Arrays.asList(
                new TimeoutHandler(),
                new ProtobufDecoder(Message.Pkt.getDefaultInstance()),
                new ProtobufEncoder(),
                new LogReadHandler(),
                new LogWriteHandler(),
                new HeartbeatHandler(),
                new LoginHandler(),
                new DevStatusHandler()
        ).toArray(new ChannelHandler[0]);
    }
}
