package test;


import cn.jinelei.rainbow.smart.client.SocketClient;
import cn.jinelei.rainbow.smart.server.SocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.After;
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

public class ClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTest.class);
    private final int WAIT_TIME = 5;
    private final boolean LOCAL_DEBUG_MODE = false;
    private final List<String> hosts = Arrays.asList("127.0.0.1", "198.181.57.207", "192.158.5.188");
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
            assert serverCond.await(WAIT_TIME, TimeUnit.SECONDS) : "server start failure";
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
                        LOGGER.debug("client {} received: login rsp: {}", ctx.channel().id(), msg);
                        loginCond.signalAll();
                    } else if (Message.Tag.HEARTBEAT.equals(pkt.getTag())
                            && pkt.hasHeartbeatRspMsg()) {
                        LOGGER.debug("client {} received: heartbeat rsp: {}", ctx.channel().id(), msg);
                        heartbeatCond.signalAll();
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
        };
    }

    @Test
    public void test() throws InterruptedException {
        lock.lock();
        assert clientCond.await(WAIT_TIME, TimeUnit.SECONDS) : "client start failure";
        lock.unlock();

        LOGGER.debug("client start login", channel.id());
        channel.pipeline().writeAndFlush(getLoginReq());
        lock.lock();
        assert loginCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch login data failure";
        lock.unlock();

        Thread.sleep(5500);

        LOGGER.debug("client query online_devices", channel.id());
        channel.pipeline().writeAndFlush(getOnlineDevicesReq());
        lock.lock();
        assert onlineCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch online_devices failure";
        lock.unlock();

        LOGGER.debug("client heartbeat", channel.id());
        channel.pipeline().writeAndFlush(getHeartbeatReq());
        lock.lock();
        assert heartbeatCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch online_devices failure";
        lock.unlock();

        LOGGER.debug("client query online_devices", channel.id());
        channel.pipeline().writeAndFlush(getOnlineDevicesReq());
        lock.lock();
        assert onlineCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch online_devices failure";
        lock.unlock();

        LOGGER.debug("client query sudden_death", channel.id());
        channel.pipeline().writeAndFlush(getSuddenDeathDevices());
        lock.lock();
        assert suddenDeathCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch sudden_death failure";
        lock.unlock();

        LOGGER.debug("client query dead", channel.id());
        channel.pipeline().writeAndFlush(getDeadDevicesReq());
        lock.lock();
        assert deadCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch dead failure";
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
