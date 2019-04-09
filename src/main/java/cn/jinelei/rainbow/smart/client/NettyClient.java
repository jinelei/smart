package cn.jinelei.rainbow.smart.client;

import cn.jinelei.rainbow.smart.utils.HandlerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import protobuf.Common;
import protobuf.Message;

import java.util.UUID;

public class NettyClient implements Runnable {
    private int port;
    private String host;
    private String mac;

    public NettyClient(int port) {
        this.port = port;
        this.host = "127.0.0.1";
        this.mac = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public NettyClient(String host, int port) {
        this.port = port;
        this.host = host;
        this.mac = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public NettyClient(int port, String host, String mac) {
        this.port = port;
        this.host = host;
        this.mac = mac;
    }

    @Override
    public void run() {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(HandlerUtils.initDecoders());
                            ch.pipeline().addLast("encoder", new ProtobufEncoder());
                        }
                    });

            Channel channel = bootstrap.connect(host, port).channel();
            int n = 5;
            while (n-- > 0) {
                Message.HeartbeatReqMsg msg = Message.HeartbeatReqMsg.newBuilder().build();

                Message.Pkt pkt = Message.Pkt.newBuilder()
                        .setDstAddr("0")
                        .setSrcAddr(this.mac)
                        .setTransportDirection(Common.TransportDirection.APP_TO_SERVER)
                        .setTag(Message.Tag.HEARTBEAT)
                        .setData(msg.toByteString())
                        .build();
                channel.pipeline().writeAndFlush(pkt);
                Thread.sleep(2000);
            }
            channel.disconnect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
