package cn.jinelei.rainbow.smart.server;

import cn.jinelei.rainbow.smart.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Message;

/**
 * @author jinelei
 */
public class SocketServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
    private int port;

    public SocketServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(TimeoutHandler.class.getSimpleName(), new TimeoutHandler());
                            ch.pipeline().addLast(ProtobufEncoder.class.getSimpleName(), new ProtobufEncoder());
                            ch.pipeline().addLast(ProtobufDecoder.class.getSimpleName(), new ProtobufDecoder(Message.Pkt.getDefaultInstance()));
                            ch.pipeline().addLast(PktHandler.class.getSimpleName(), new PktHandler());
                            ch.pipeline().addLast(HeartbeatHandler.class.getSimpleName(), new HeartbeatHandler());
                            ch.pipeline().addLast(LoginHandler.class.getSimpleName(), new LoginHandler());
                            ch.pipeline().addLast(DevStatusHandler.class.getSimpleName(), new DevStatusHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

}
