package cn.jinelei.rainbow.smart;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.jinelei.rainbow.smart.handler.EchoHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TestEcho {
    private static final int port = 8000;
    private static final String host = "127.0.0.1";
    private static final int TIMEOUT = 10;
    private static final String message = "hello";

    private static ServerBootstrap serverBootstrap;
    private static Bootstrap clientBootstrap;
    private static NioEventLoopGroup boss, worker, client;
    private static ChannelFuture serverFuture, clientFuture;

    private static final CountDownLatch readFinish = new CountDownLatch(1);

    @BeforeClass
    public static void initServer() throws InterruptedException {
        // 初始化服务器
        serverBootstrap = new ServerBootstrap();
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true).childHandler(initServerHandler());
        serverFuture = serverBootstrap.bind(port).sync();
        serverFuture.channel().closeFuture().addListener(future -> {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        });

        // 初始化客户端
        clientBootstrap = new Bootstrap();
        client = new NioEventLoopGroup();
        clientBootstrap.group(client).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
                .handler(initClientHandler());
        clientFuture = clientBootstrap.connect(host, port).sync();
        clientFuture.channel().closeFuture().addListener(future -> client.shutdownGracefully());
    }

    // 初始化服务器Handler
    private static ChannelInitializer<SocketChannel> initServerHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new EchoHandler());
            }
        };
    }

    // 初始化客户端Handler
    public static ChannelInitializer<SocketChannel> initClientHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new StringDecoder());
                // ch.pipeline().addLast(new EchoHandler());
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof String && message.equals(msg))
                            readFinish.countDown();
                        else
                            ctx.fireChannelRead(msg);
                    }
                });
            }
        };
    }

    @Test
    public void test() throws InterruptedException {
        clientFuture.channel().writeAndFlush(message);
        Assert.assertTrue(readFinish.await(TIMEOUT, TimeUnit.SECONDS));
    }

    @AfterClass
    public static void destory() {
        if (client != null && !client.isShutdown())
            client.shutdownGracefully();
        if (boss != null && !boss.isShutdown())
            boss.shutdownGracefully();
        if (worker != null && !worker.isShutdown())
            worker.shutdownGracefully();
    }
}
