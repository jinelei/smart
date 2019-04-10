package test;


import cn.jinelei.rainbow.smart.client.SocketClient;
import cn.jinelei.rainbow.smart.server.SocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.*;

public class NettyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyTest.class);

    @Test
    public void testClient() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new SocketServer(8000));
        SocketClient socketClient = new SocketClient(8000, "127.0.0.1", new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                LOGGER.debug("client {} received: {}", ctx.channel().id(), ((Message.Pkt) msg).toString());
            }
        });
        FutureTask<Channel> futureTask = new FutureTask<>(socketClient);
        FutureTask<Channel> futureTask1 = new FutureTask<>(new SocketClient(8000, "127.0.0.1", null));

        executor.execute(futureTask1);
        executor.execute(futureTask);

        try {
            Channel channel1 = futureTask1.get(5, TimeUnit.SECONDS);
            channel1.pipeline().writeAndFlush(
                    Message.Pkt.newBuilder()
                            .setDstAddr("0")
                            .setSrcAddr(socketClient.mac)
                            .setDir(true)
                            .setTag(Message.Tag.LOGIN)
                            .setSeq(1)
                            .setTimestamp(new Date().getTime())
                            .setLoginReqMsg(
                                    Message.LoginReqMsg.newBuilder()
                                            .setTimeout(3)
                                            .addAllDevFetures(Arrays.asList(Common.DevFeature.SPEAKER,
                                                    Common.DevFeature.MICROPHONE))
                            ).build()
            );
            channel1.disconnect();

            Channel channel = futureTask.get(5, TimeUnit.SECONDS);

            Message.Pkt pkt = Message.Pkt.newBuilder()
                    .setDstAddr("0")
                    .setSrcAddr(socketClient.mac)
                    .setDir(true)
                    .setTag(Message.Tag.LOGIN)
                    .setSeq(1)
                    .setTimestamp(new Date().getTime())
                    .setLoginReqMsg(
                            Message.LoginReqMsg.newBuilder()
                                    .setTimeout(3)
                                    .addAllDevFetures(Arrays.asList(Common.DevFeature.SPEAKER,
                                            Common.DevFeature.MICROPHONE))
                    ).build();

            LOGGER.debug("client {} login", channel.id());
            channel.pipeline().writeAndFlush(pkt);

            LOGGER.debug("client {} query dev_status", channel.id());
            channel.pipeline().writeAndFlush(Message.Pkt.newBuilder()
                    .setDstAddr("0")
                    .setSrcAddr(socketClient.mac)
                    .setDir(true)
                    .setTag(Message.Tag.DEV_STATUS)
                    .setOnlineDevicesReqMsg(Message.OnlineDevicesReqMsg.newBuilder().build())
                    .build()
            );

            socketClient.sleep(7000);

            LOGGER.debug("client {} query dev_status", channel.id());
            channel.pipeline().writeAndFlush(Message.Pkt.newBuilder()
                    .setDstAddr("0")
                    .setSrcAddr(socketClient.mac)
                    .setDir(true)
                    .setTag(Message.Tag.DEV_STATUS)
                    .setSuddenDeathDevicesReqMsg(Message.SuddenDeathDevicesReqMsg.newBuilder().build())
                    .build()
            );

            socketClient.sleep(10000);

            LOGGER.debug("client {} query dev_status", channel.id());
            channel.pipeline().writeAndFlush(Message.Pkt.newBuilder()
                    .setDstAddr("0")
                    .setSrcAddr(socketClient.mac)
                    .setDir(true)
                    .setTag(Message.Tag.DEV_STATUS)
                    .setDeadDevicesReqMsg(Message.DeadDevicesReqMsg.newBuilder().build())
                    .build()
            );

            socketClient.sleep(10000);
            LOGGER.debug("end");

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
