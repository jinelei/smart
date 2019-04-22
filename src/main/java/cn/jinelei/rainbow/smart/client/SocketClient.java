package cn.jinelei.rainbow.smart.client;

import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jinelei.rainbow.smart.utils.HandlerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SocketClient implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);
    private int port;
    private String host;
    public String mac;
    private ChannelHandler channelHandler;
    private Consumer<ChannelFuture> consumer;

    private Lock lock = new ReentrantLock();
    private Condition bindCond = lock.newCondition();

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
        ChannelFuture future = bootstrap.connect(host, port).addListener(future1 -> {
            lock.lock();
            bindCond.signalAll();
            lock.unlock();
        });

        lock.lock();
        bindCond.awaitUninterruptibly();
        lock.unlock();
        if (consumer != null)
            consumer.accept(future);

        future.channel().closeFuture().addListener(future12 -> {
            group.shutdownGracefully();
        });
    }

}
