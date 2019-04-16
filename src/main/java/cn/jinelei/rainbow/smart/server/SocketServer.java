package cn.jinelei.rainbow.smart.server;

import cn.jinelei.rainbow.smart.utils.HandlerUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
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
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(HandlerUtils.init());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = null;
        try {
            future = bootstrap.bind(port).sync();
            LOGGER.info("server listen {}", port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (consumer != null)
            consumer.accept(future);

        future.channel().closeFuture().addListener(future12 -> {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        });
    }

    public static void main(String[] args) {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
        new SocketServer(8000).run();
    }
}
