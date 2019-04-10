package cn.jinelei.rainbow.smart.client;

import cn.jinelei.rainbow.smart.utils.HandlerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Message;

import java.util.UUID;
import java.util.concurrent.Callable;

public class SocketClient implements Callable<Channel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);
    private int port;
    private String host;
    public String mac;
    private ChannelHandler channelHandler;

    public SocketClient(int port, String host, ChannelHandler channelHandler) {
        this.port = port;
        this.host = host;
        this.mac = UUID.randomUUID().toString().replaceAll("-", "");
        this.channelHandler = channelHandler;
    }

    public void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
    }

    @Override
    public Channel call() {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(HandlerUtils.init());
                        if (channelHandler != null) {
                            ch.pipeline().addLast(channelHandler);
                        }
                    }
                });
        return bootstrap.connect(host, port).channel();
    }
}
