package test.function;


import io.netty.channel.Channel;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestTemplate.class);
    private static final int WAIT_TIME = 5;
    private static final List<String> hosts = Arrays.asList("127.0.0.1", "198.181.57.207", "192.168.5.188");
    private static final String HOST = hosts.get(0);
    private static final int PORT = 8000;

    private static ExecutorService executor;
    private Channel channel;

    private static Lock lock = new ReentrantLock();
    private static Condition serverCond = lock.newCondition();
    private static Condition clientCond = lock.newCondition();
    private static Condition finishCond = lock.newCondition();

    @BeforeClass
    public static void beforeClass() {
//        executor = Executors.newFixedThreadPool(2);
//        Assert.assertNotNull(executor);
//        if (HOST == "127.0.0.1") {
//            executor.submit(new SocketServer(PORT, channelFuture -> {
//                lock.lock();
//                serverCond.signalAll();
//                lock.unlock();
//            }));
//        }
    }

    @Before
    public void before() throws InterruptedException {
//        if (HOST == "127.0.0.1") {
//            lock.lock();
//            Assert.assertTrue(serverCond.await(WAIT_TIME, TimeUnit.SECONDS));
//            lock.unlock();
//        }
//
//        SocketClient socketClient = new SocketClient(PORT, HOST, new ChannelInboundHandlerAdapter() {
//            @Override
//            public void channelRead(ChannelHandlerContext ctx, Object msg) {
//                if (msg != null)
//                    res[0] = (Message.Pkt) msg;
//                lock.lock();
//                finishCond.signalAll();
//                lock.unlock();
//            }
//        }, channelFuture -> {
//            this.channel = channelFuture.channel();
//            lock.lock();
//            clientCond.signalAll();
//            lock.unlock();
//        });
//
//        Assert.assertNotNull(socketClient);
//        executor.submit(socketClient);
//
//        lock.lock();
//        Assert.assertTrue(clientCond.await(WAIT_TIME, TimeUnit.SECONDS));
//        lock.unlock();
    }

    @Test
    public void test() throws InterruptedException {
//        Message.Pkt pkt = Message.Pkt.newBuilder()
//                .setSrcAddr(UUID.randomUUID().toString().replaceAll("-", ""))
//                .setDstAddr("0")
//                .setSeq(0)
//                .setDir(true)
//                .setTag(Message.Tag.LOGIN)
//                .setTimestamp(Instant.now().toEpochMilli())
//                .setLoginReqMsg(Message.LoginReqMsg.newBuilder()
//                        .setTimeout(5)
//                        .addAllDevFetures(Arrays.asList(Common.DevFeature.SPEAKER,
//                                Common.DevFeature.MICROPHONE))
//                        .build())
//                .build();
//
//        channel.writeAndFlush(pkt);
//
//        lock.lock();
//        Assert.assertTrue(finishCond.await(WAIT_TIME, TimeUnit.SECONDS));
//        lock.unlock();
//
//        Assert.assertNotNull(res[0]);
//        Assert.assertFalse(res[0].getDir());
//        Assert.assertEquals(pkt.getSeq() + 1, res[0].getSeq());
//        Assert.assertTrue(pkt.getTimestamp() < res[0].getTimestamp());
//
//        Assert.assertEquals(Message.Tag.LOGIN, res[0].getTag());
//        Assert.assertTrue(res[0].hasLoginRspMsg());
    }

    @After
    public void after() {
//        Assert.assertNotNull(channel);
//        Assert.assertTrue(channel.isOpen());
//        channel.disconnect();
    }

    @AfterClass
    public static void afterClass() {
//        Assert.assertNotNull(executor);
//        Assert.assertFalse(executor.isShutdown());
//        executor.shutdown();
    }

}
