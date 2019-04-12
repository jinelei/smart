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
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTest.class);
    private final int WAIT_TIME = 10;
//    private final String HOST = "127.0.0.1";
        private final String HOST = "198.181.57.207";
    private final int PORT = 8000;

    private ExecutorService executor;
    private Channel channel;
    private SocketClient socketClient;

    private Lock lock;
    private Condition loginCond, onlineCond, suddenDeathCond, deadCond;

    @Before
    public void init() throws InterruptedException, ExecutionException, TimeoutException {
        lock = new ReentrantLock();
        loginCond = lock.newCondition();
        onlineCond = lock.newCondition();
        suddenDeathCond = lock.newCondition();
        deadCond = lock.newCondition();
        executor = Executors.newFixedThreadPool(3);
        socketClient = new SocketClient(PORT, HOST, new ChannelInboundHandlerAdapter() {
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

        if (HOST == "127.0.0.1")
            executor.submit(new SocketServer(PORT));

        FutureTask<Channel> futureTask = new FutureTask<>(socketClient);
        executor.execute(futureTask);
        channel = futureTask.get(WAIT_TIME, TimeUnit.SECONDS);
        assert channel != null : "can not open channel";
        LOGGER.debug("init");
    }

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(2000);
        LOGGER.debug("client start login", channel.id());
        assert channel != null : "can not open channel";
        channel.pipeline().writeAndFlush(
                Message.Pkt.newBuilder()
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
                        .build()
        );
        lock.lock();
        assert loginCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch login data failure";
        lock.unlock();

        LOGGER.debug("client query online_devices", channel.id());
        channel.pipeline().writeAndFlush(
                Message.Pkt.newBuilder()
                        .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setDstAddr("0")
                        .setSeq(0)
                        .setDir(true)
                        .setTag(Message.Tag.DEV_STATUS)
                        .setTimestamp(Instant.now().toEpochMilli())
                        .setOnlineDevicesReqMsg(Message.OnlineDevicesReqMsg.newBuilder().build())
                        .build()
        );
        lock.lock();
        assert onlineCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch online_devices failure";
        lock.unlock();

        LOGGER.debug("client query sudden_death", channel.id());
        channel.pipeline().writeAndFlush(
                Message.Pkt.newBuilder()
                        .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setDstAddr("0")
                        .setSeq(0)
                        .setDir(true)
                        .setTag(Message.Tag.DEV_STATUS)
                        .setTimestamp(Instant.now().toEpochMilli())
                        .setSuddenDeathDevicesReqMsg(Message.SuddenDeathDevicesReqMsg.newBuilder().build())
                        .build()
        );
        lock.lock();
        assert suddenDeathCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch sudden_death failure";
        lock.unlock();

        LOGGER.debug("client query dead", channel.id());
        channel.pipeline().writeAndFlush(
                Message.Pkt.newBuilder()
                        .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setDstAddr("0")
                        .setSeq(0)
                        .setDir(true)
                        .setTag(Message.Tag.DEV_STATUS)
                        .setTimestamp(Instant.now().toEpochMilli())
                        .setDeadDevicesReqMsg(Message.DeadDevicesReqMsg.newBuilder().build())
                        .build()
        );
        lock.lock();
        assert deadCond.await(WAIT_TIME, TimeUnit.SECONDS) : "fetch dead failure";
        lock.unlock();
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
