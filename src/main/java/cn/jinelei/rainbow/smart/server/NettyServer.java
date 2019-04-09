package cn.jinelei.rainbow.smart.server;

import cn.jinelei.rainbow.smart.server.handler.HeartbeatHandler;
import cn.jinelei.rainbow.smart.server.handler.PktHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.jinelei.rainbow.smart.utils.HandlerUtils.initDecoders;


/**
 * @author jinelei
 */
public class NettyServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
    private int port;

    public NettyServer(int port) {
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
                            ch.pipeline().addLast("encoder", new ProtobufEncoder());
                            ch.pipeline().addLast(initDecoders());
                            ch.pipeline().addLast(new PktHandler());
                            ch.pipeline().addLast(new HeartbeatHandler());
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
