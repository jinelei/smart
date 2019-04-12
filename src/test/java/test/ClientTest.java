package test;


import cn.jinelei.rainbow.smart.client.SocketClient;
import cn.jinelei.rainbow.smart.server.SocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.fail;

public class ClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTest.class);
    private final int WAIT_TIME = 10;
    private final List<String> hosts = Arrays.asList("127.0.0.1", "198.181.57.207", "192.168.5.188");
    private final String HOST = hosts.get(0);
    private final int PORT = 8000;

    private ExecutorService executor;
    private Channel channel;

    private Lock lock = new ReentrantLock();
    private Condition serverCond = lock.newCondition();
    private Condition clientCond = lock.newCondition();
    private Condition loginCond = lock.newCondition();
    private Condition onlineCond = lock.newCondition();
    private Condition suddenDeathCond = lock.newCondition();
    private Condition deadCond = lock.newCondition();
    private Condition heartbeatCond = lock.newCondition();

    @Before
    public void init() throws InterruptedException {
        executor = Executors.newFixedThreadPool(2);
        if (HOST == "127.0.0.1") {
            executor.submit(new SocketServer(PORT, channelFuture -> {
                lock.lock();
                serverCond.signalAll();
                lock.unlock();
            }));
        }

        SocketClient socketClient = new SocketClient(PORT, HOST, initClientAction(), channelFuture -> {
            lock.lock();
            clientCond.signalAll();
            this.channel = channelFuture.channel();
            lock.unlock();
        });

        if (HOST == "127.0.0.1") {
            lock.lock();
            Assert.assertTrue("server start failure", serverCond.await(WAIT_TIME, TimeUnit.SECONDS));
            executor.submit(socketClient);
            lock.unlock();
        } else {
            executor.submit(socketClient);
        }

        LOGGER.debug("init");
    }

    private ChannelInboundHandlerAdapter initClientAction() {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                lock.lock();
                if (msg instanceof Message.Pkt
                        && !((Message.Pkt) msg).getDir()) {
                    Message.Pkt pkt = (Message.Pkt) msg;
                    if (Message.Tag.LOGIN.equals(pkt.getTag())
                            && pkt.hasLoginRspMsg()) {
                        loginCond.signalAll();
                    } else if (Message.Tag.HEARTBEAT.equals(pkt.getTag())
                            && pkt.hasHeartbeatRspMsg()) {
                        heartbeatCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasOnlineDevicesRspMsg()) {
                        onlineCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasSuddenDeathDevicesRspMsg()) {
                        suddenDeathCond.signalAll();
                    } else if (Message.Tag.DEV_STATUS.equals(pkt.getTag())
                            && pkt.hasDeadDevicesRspMsg()) {
                        deadCond.signalAll();
                    } else {
                        fail(String.format("client {} received unknown data: {}", ctx.channel().id(), msg.toString()));
                    }
                }
                lock.unlock();
            }
        };
    }

    @Test
    public void test() throws InterruptedException {
        lock.lock();
        Assert.assertTrue("client start failure", clientCond.await(WAIT_TIME, TimeUnit.SECONDS));
        lock.unlock();

        channel.pipeline().writeAndFlush(getLoginReq());
        lock.lock();
        Assert.assertTrue("fetch login data failure", loginCond.await(WAIT_TIME, TimeUnit.SECONDS));
        lock.unlock();

        Thread.sleep(5500);

        channel.pipeline().writeAndFlush(getOnlineDevicesReq());
        lock.lock();
        Assert.assertTrue("fetch online_devices failure", onlineCond.await(WAIT_TIME, TimeUnit.SECONDS));
        lock.unlock();

        channel.pipeline().writeAndFlush(getHeartbeatReq());
        lock.lock();
        Assert.assertTrue("fetch online_devices failure", heartbeatCond.await(WAIT_TIME, TimeUnit.SECONDS));
        lock.unlock();

        channel.pipeline().writeAndFlush(getOnlineDevicesReq());
        lock.lock();
        Assert.assertTrue("fetch online_devices failure", onlineCond.await(WAIT_TIME, TimeUnit.SECONDS));
        lock.unlock();

        channel.pipeline().writeAndFlush(getSuddenDeathDevices());
        lock.lock();
        Assert.assertTrue("fetch sudden_death failure", suddenDeathCond.await(WAIT_TIME, TimeUnit.SECONDS));
        lock.unlock();

        channel.pipeline().writeAndFlush(getDeadDevicesReq());
        lock.lock();
        Assert.assertTrue("fetch dead failure", deadCond.await(WAIT_TIME, TimeUnit.SECONDS));
        lock.unlock();
    }

    private Message.Pkt getHeartbeatReq() {
        return Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.HEARTBEAT)
                .setTimestamp(Instant.now().toEpochMilli())
                .setHeartbeatReqMsg(Message.HeartbeatReqMsg.newBuilder().build())
                .build();
    }

    private Message.Pkt getDeadDevicesReq() {
        return Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.DEV_STATUS)
                .setTimestamp(Instant.now().toEpochMilli())
                .setDeadDevicesReqMsg(Message.DeadDevicesReqMsg.newBuilder().build())
                .build();
    }

    private Message.Pkt getSuddenDeathDevices() {
        return Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.DEV_STATUS)
                .setTimestamp(Instant.now().toEpochMilli())
                .setSuddenDeathDevicesReqMsg(Message.SuddenDeathDevicesReqMsg.newBuilder().build())
                .build();
    }

    private Message.Pkt getOnlineDevicesReq() {
        return Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.DEV_STATUS)
                .setTimestamp(Instant.now().toEpochMilli())
                .setOnlineDevicesReqMsg(Message.OnlineDevicesReqMsg.newBuilder().build())
                .build();
    }

    private Message.Pkt getLoginReq() {
        return Message.Pkt.newBuilder()
                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                .setDstAddr("0")
                .setSeq(0)
                .setDir(true)
                .setTag(Message.Tag.LOGIN)
                .setTimestamp(Instant.now().toEpochMilli())
                .setLoginReqMsg(Message.LoginReqMsg.newBuilder()
                        .setTimeout(5)
                        .addAllDevFetures(Arrays.asList(Common.DevFeature.SPEAKER,
                                Common.DevFeature.MICROPHONE))
                        .build())
                .build();
    }

    @After
    public void finish() {
        LOGGER.debug("finish");
        assert channel != null;
        channel.disconnect();
        assert executor != null;
        executor.shutdown();
    }

}
