package tdd.state;

import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jinelei.rainbow.smart.handler.DevStatusHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class TestLoginSuccess {
    private static final int port = 8090;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestLoginSuccess.class);
    ServerBootstrap serverBootstrap;
    Bootstrap clientBootstrap;
    NioEventLoopGroup serverBossGroup;
    NioEventLoopGroup serverWorkerGroup;
    EventLoopGroup clientGroup;
    ChannelFuture clientChannel;
    ChannelFuture serverChannel;

    CountDownLatch serverStartup = new CountDownLatch(1);
    CountDownLatch clientStartup = new CountDownLatch(1);

    @BeforeClass
    public void initServerAndClient() throws InterruptedException {
        serverBootstrap = new ServerBootstrap();
        clientBootstrap = new Bootstrap();
        serverBossGroup = new NioEventLoopGroup();
        serverWorkerGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();

        serverBootstrap.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast("stateHandler", new DevStatusHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        serverChannel = serverBootstrap.bind(port).sync();
        serverChannel.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    serverBossGroup.shutdownGracefully();
                    serverWorkerGroup.shutdownGracefully();
                    LOGGER.debug("future channel close success");
                } else
                    LOGGER.debug("future channel close success");
            }
        });

        clientBootstrap.group(clientGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new StringDecoder());
            }
        });

        clientChannel = clientBootstrap.connect("127.0.0.1", port)
                .addListener(future2 -> LOGGER.debug("connect success"));

        clientChannel.channel().closeFuture().addListener(future3 -> clientGroup.shutdownGracefully());

    }

    @Test
    public void tesSuccess() {
        clientChannel.channel().writeAndFlush("hello");

    }

    @AfterClass
    public void shutdown() {
        if (!clientGroup.isShutdown()) {
            clientGroup.shutdownGracefully();
        }
        if (!serverBossGroup.isShutdown()) {
            serverBossGroup.shutdownGracefully();
        }
        if (!serverWorkerGroup.isShutdown()) {
            serverWorkerGroup.shutdownGracefully();
        }
    }
}