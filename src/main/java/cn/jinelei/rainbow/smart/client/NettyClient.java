package cn.jinelei.rainbow.smart.client;

import cn.jinelei.rainbow.smart.utils.HandlerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.util.UUID;

public class NettyClient implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
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
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(HandlerUtils.initDecoders());
                            ch.pipeline().addLast("encoder", new ProtobufEncoder());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                    LOGGER.debug("received: {}", msg);
                                    if (msg instanceof Message.Pkt
                                            && Message.Tag.HEARTBEAT.equals(((Message.Pkt) msg).getTag())) {
                                        Message.Pkt pkt = (Message.Pkt) msg;
                                        if (pkt.hasHeartbeatRspMsg()) {
                                            LOGGER.debug("data: {} ", pkt.getHeartbeatRspMsg());
                                        }
                                    }
                                }
                            });
                        }
                    });

            Channel channel = bootstrap.connect(host, port).channel();
            int n = 5;
            while (n-- > 0) {
                Message.Pkt pkt = Message.Pkt.newBuilder()
                        .setDstAddr("0")
                        .setSrcAddr(this.mac)
                        .setDir(true)
                        .setTag(Message.Tag.HEARTBEAT)
                        .setSeq(1)
                        .setHeartbeatReqMsg(
                                Message.HeartbeatReqMsg.newBuilder().build()
                        )
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
