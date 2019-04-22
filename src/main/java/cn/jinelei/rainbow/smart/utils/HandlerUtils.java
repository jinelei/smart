package cn.jinelei.rainbow.smart.utils;

import java.util.Arrays;

import cn.jinelei.rainbow.smart.server.coder.JySmartProtoDecoder;
import cn.jinelei.rainbow.smart.server.coder.JySmartProtoEncoder;
import cn.jinelei.rainbow.smart.server.handler.LogReadHandler;
import cn.jinelei.rainbow.smart.server.handler.LogWriteHandler;
import cn.jinelei.rainbow.smart.server.handler.TimeoutHandler;
import io.netty.channel.ChannelHandler;

/**
 * @author jinelei
 */
public class HandlerUtils {

    public static ChannelHandler[] init() {
        return Arrays.asList(new TimeoutHandler(), new JySmartProtoEncoder(), new JySmartProtoDecoder(),
                new LogReadHandler(), new LogWriteHandler()
        // new HeartbeatHandler(),
        // new LoginHandler(),
        // new DevStatusHandler()
        ).toArray(new ChannelHandler[0]);
    }
}
