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

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyTest.class);
    private final String host = "127.0.0.1";
    private final int port = 8000;
    private final Lock lock = new ReentrantLock();
    private final Condition loginCond = lock.newCondition();
    private final Condition onlineCond = lock.newCondition();
    private final Condition suddenDeathCond = lock.newCondition();
    private final Condition deadCond = lock.newCondition();

    public Message.Pkt loginReqMsg() {
        Message.Pkt pkt = Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.LOGIN)
                .setTimestamp(Instant.now().toEpochMilli())
                .setLoginReqMsg(Message.LoginReqMsg.newBuilder()
                        .setTimeout(60)
                        .addAllDevFetures(Arrays.asList(Common.DevFeature.SPEAKER,
                                Common.DevFeature.MICROPHONE))
                        .build())
                .build();
        System.out.println(Arrays.toString(pkt.toByteArray()));
        return pkt;
    }

    public Message.Pkt onlineDevicesReqMsg() {
        Message.Pkt pkt = Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.DEV_STATUS)
                .setTimestamp(Instant.now().toEpochMilli())
                .setOnlineDevicesReqMsg(Message.OnlineDevicesReqMsg.newBuilder().build())
                .build();
        System.out.println(Arrays.toString(pkt.toByteArray()));
        return pkt;
    }

    public Message.Pkt suddenDeathDevicesReqMsg() {
        Message.Pkt pkt = Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.DEV_STATUS)
                .setTimestamp(Instant.now().toEpochMilli())
                .setSuddenDeathDevicesReqMsg(Message.SuddenDeathDevicesReqMsg.newBuilder().build())
                .build();
        System.out.println(Arrays.toString(pkt.toByteArray()));
        return pkt;
    }

    public Message.Pkt deadDevicesReqMsg() {
        Message.Pkt pkt = Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.DEV_STATUS)
                .setTimestamp(Instant.now().toEpochMilli())
                .setDeadDevicesReqMsg(Message.DeadDevicesReqMsg.newBuilder().build())
                .build();
        System.out.println(Arrays.toString(pkt.toByteArray()));
        return pkt;
    }

    @Test
    public void testLocal() throws InterruptedException, ExecutionException, TimeoutException {
        final String host = "127.0.0.1";
        final int port = 8000;
        final Lock lock = new ReentrantLock();
        final Condition loginCond = lock.newCondition();
        final Condition onlineCond = lock.newCondition();
        final Condition suddenDeathCond = lock.newCondition();
        final Condition deadCond = lock.newCondition();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new SocketServer(port));
        SocketClient socketClient = new SocketClient(port, host, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                lock.lock();
                if (msg instanceof Message.Pkt
                        && !((Message.Pkt) msg).getDir()) {
                    Message.Pkt pkt = (Message.Pkt) msg;
                    if (Message.Tag.LOGIN.equals(pkt.getTag())
                            && pkt.hasLoginRspMsg()) {
                        LOGGER.debug("client {} received: login rsp: {}", ctx.channel().id(), msg);
                        loginCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasOnlineDevicesRspMsg()) {
                        LOGGER.debug("client {} received: online_devices rsp: {}", ctx.channel().id(), msg);
                        onlineCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasSuddenDeathDevicesRspMsg()) {
                        LOGGER.debug("client {} received: sudden_death_devices rsp: {}", ctx.channel().id(), msg);
                        suddenDeathCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasDeadDevicesRspMsg()) {
                        LOGGER.debug("client {} received: dead_devices rsp: {}", ctx.channel().id(), msg);
                        deadCond.signalAll();
                    } else {
                        LOGGER.debug("client {} received: {}", ctx.channel().id(), ((Message.Pkt) msg).toString());
                    }
                }
                lock.unlock();
            }
        });
        FutureTask<Channel> futureTask = new FutureTask<>(socketClient);
        executor.execute(futureTask);
        Channel channel = futureTask.get(2, TimeUnit.SECONDS);
        if (channel == null) {
            LOGGER.error("can not fetch channel");
            executor.shutdown();
        }
        LOGGER.debug("client start login", channel.id());
        channel.pipeline().writeAndFlush(loginReqMsg());

        lock.lock();
        if (loginCond.await(3, TimeUnit.SECONDS)) {
            LOGGER.debug("client login success");
            channel.pipeline().writeAndFlush(onlineDevicesReqMsg());
            if (onlineCond.await(3, TimeUnit.SECONDS)) {
                LOGGER.debug("client online_devices success");
                channel.pipeline().writeAndFlush(suddenDeathDevicesReqMsg());
                if (suddenDeathCond.await(3, TimeUnit.SECONDS)) {
                    LOGGER.debug("client sudden_death_devices success");
                    channel.pipeline().writeAndFlush(deadDevicesReqMsg());
                    if (deadCond.await(3, TimeUnit.SECONDS)) {
                        LOGGER.debug("client dead_devices success");
                    } else {
                        LOGGER.debug("client dead_devices failure");
                    }
                } else {
                    LOGGER.debug("client sudden_death_devices failure");
                }
            } else {
                LOGGER.debug("client online_devices failure");
            }
        } else {
            LOGGER.debug("client login failure");
        }
        lock.unlock();
        LOGGER.debug("end");
        executor.shutdown();
    }

    @Test
    public void testRemote() throws InterruptedException, ExecutionException, TimeoutException {
//        final String host = "198.181.57.207";
        final String host = "192.168.5.188";
        final int port = 8000;
        final Lock lock = new ReentrantLock();
        final Condition loginCond = lock.newCondition();
        final Condition onlineCond = lock.newCondition();
        final Condition suddenDeathCond = lock.newCondition();
        final Condition deadCond = lock.newCondition();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        SocketClient socketClient = new SocketClient(port, host, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                lock.lock();
                if (msg instanceof Message.Pkt
                        && !((Message.Pkt) msg).getDir()) {
                    Message.Pkt pkt = (Message.Pkt) msg;
                    if (Message.Tag.LOGIN.equals(pkt.getTag())
                            && pkt.hasLoginRspMsg()) {
                        LOGGER.debug("client {} received: login rsp: {}", ctx.channel().id(), msg);
                        loginCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasOnlineDevicesRspMsg()) {
                        LOGGER.debug("client {} received: online_devices rsp: {}", ctx.channel().id(), msg);
                        onlineCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasSuddenDeathDevicesRspMsg()) {
                        LOGGER.debug("client {} received: sudden_death_devices rsp: {}", ctx.channel().id(), msg);
                        suddenDeathCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasDeadDevicesRspMsg()) {
                        LOGGER.debug("client {} received: dead_devices rsp: {}", ctx.channel().id(), msg);
                        deadCond.signalAll();
                    } else {
                        LOGGER.debug("client {} received: {}", ctx.channel().id(), ((Message.Pkt) msg).toString());
                    }
                }
                lock.unlock();
            }
        });
        FutureTask<Channel> futureTask = new FutureTask<>(socketClient);
        executor.execute(futureTask);
        Channel channel = futureTask.get(2, TimeUnit.SECONDS);
        if (channel == null) {
            LOGGER.error("can not fetch channel");
            executor.shutdown();
        }
        LOGGER.debug("client start login", channel.id());
        channel.pipeline().writeAndFlush(loginReqMsg());

        lock.lock();
        if (loginCond.await(10, TimeUnit.SECONDS))
            LOGGER.debug("client login success");
        else
            LOGGER.debug("client login failure");

        channel.pipeline().writeAndFlush(onlineDevicesReqMsg());
        if (onlineCond.await(7, TimeUnit.SECONDS))
            LOGGER.debug("client online_devices success");
        else
            LOGGER.debug("client online_devices failure");

        channel.pipeline().writeAndFlush(suddenDeathDevicesReqMsg());
        if (suddenDeathCond.await(5, TimeUnit.SECONDS))
            LOGGER.debug("client sudden_death_devices success");
        else
            LOGGER.debug("client sudden_death_devices failure");

        channel.pipeline().writeAndFlush(deadDevicesReqMsg());
        if (deadCond.await(5, TimeUnit.SECONDS))
            LOGGER.debug("client dead_devices success");
        else
            LOGGER.debug("client dead_devices failure");

        lock.unlock();
        LOGGER.debug("end");
        executor.shutdown();
    }

}
