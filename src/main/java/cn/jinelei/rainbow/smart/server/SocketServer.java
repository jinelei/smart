package cn.jinelei.rainbow.smart.server;

import cn.jinelei.rainbow.smart.utils.HandlerUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author jinelei
 */
public class SocketServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
    private int port;
    private Consumer<ChannelFuture> consumer;

    private Lock lock = new ReentrantLock();
    private Condition bindCond = lock.newCondition();

    public SocketServer(int port) {
        this.port = port;
    }

    public SocketServer(int port, Consumer<ChannelFuture> consumer) {
        this.port = port;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(HandlerUtils.init());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.bind(port).addListener(future1 -> {
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
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        });
    }

    public static void main(String[] args) {
        new SocketServer(8000).run();
    }
}
