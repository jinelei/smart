package cn.jinelei.rainbow.smart.server.netty;

import cn.jinelei.rainbow.smart.protobuf.MessageProto;
import cn.jinelei.rainbow.smart.server.netty.handler.HandlerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.Date;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(HandlerUtils.initDecoders());
                        ch.pipeline().addLast("encoder", new ProtobufEncoder());
                    }
                });

        Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();
        int n = 5;
        while (n-- > 0) {
            MessageProto.HeartBeat heartBeat = MessageProto.HeartBeat.newBuilder().setId(112).setNext(60).build();
            channel.pipeline().writeAndFlush(heartBeat);
            Thread.sleep(2000);
        }
        channel.disconnect();
        group.shutdownGracefully();
    }
}
