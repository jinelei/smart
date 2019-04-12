package cn.jinelei.rainbow.smart.client;

import cn.jinelei.rainbow.smart.utils.HandlerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.function.Consumer;

public class SocketClient implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);
    private int port;
    private String host;
    public String mac;
    private ChannelHandler channelHandler;
    private Consumer<ChannelFuture> consumer;

    public SocketClient(int port, String host, ChannelHandler channelHandler, Consumer<ChannelFuture> consumer) {
        this.port = port;
        this.host = host;
        this.mac = UUID.randomUUID().toString().replaceAll("-", "");
        this.channelHandler = channelHandler;
        this.consumer = consumer;
    }

    @Override
    public void run() {
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
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            if (consumer != null)
                consumer.accept(channelFuture);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
