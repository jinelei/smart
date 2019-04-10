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

/**
 * @author jinelei
 */
public class SocketServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
    private int port;
    public long pid;

    public SocketServer(int port) {
        this.port = port;
        LOGGER.debug("set pid: {}", Thread.currentThread().getId());
        pid = Thread.currentThread().getId();
    }

    @Override
    public void run() {
        LOGGER.debug("reset pid: {}", Thread.currentThread().getId());
        this.pid = Thread.currentThread().getId();
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
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
            ChannelFuture future = bootstrap.bind(port).sync();
            synchronized (this) {
                this.notifyAll();
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
