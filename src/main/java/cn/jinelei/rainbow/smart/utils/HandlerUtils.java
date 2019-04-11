package cn.jinelei.rainbow.smart.utils;

import cn.jinelei.rainbow.smart.server.handler.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import protobuf.Message;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jinelei
 */
public class HandlerUtils {

    public static ChannelHandler[] init() {
        return Arrays.asList(
                new TimeoutHandler(),
                new ProtobufDecoder(Message.Pkt.getDefaultInstance()),
                new StringDecoder(Charset.defaultCharset()),
                new ProtobufEncoder(),
                new StringEncoder(Charset.defaultCharset()),
                new PktHandler(),
                new HeartbeatHandler(),
                new LoginHandler(),
                new DevStatusHandler()
        ).toArray(new ChannelHandler[0]);
    }
}
