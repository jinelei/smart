package test.function;

import cn.jinelei.rainbow.smart.client.SocketClient;
import cn.jinelei.rainbow.smart.model.L1Bean;
import cn.jinelei.rainbow.smart.server.SocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestProto {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestProto.class);
    private static final int WAIT_TIME = 5;
    private static final List<String> hosts = Arrays.asList("127.0.0.1", "198.181.57.207", "192.168.5.188");
    private static final String HOST = hosts.get(0);
    private static final int PORT = 8000;

    private static ExecutorService executor;
    private Channel channel;

    public static final CountDownLatch serverCond = new CountDownLatch(1);
    public static final CountDownLatch clientCond = new CountDownLatch(1);
    public static final CountDownLatch finishCond = new CountDownLatch(1);

    @BeforeClass
    public static void beforeClass() {
        executor = Executors.newFixedThreadPool(2);
        Assert.assertNotNull(executor);
        if (HOST == "127.0.0.1") {
            executor.submit(new SocketServer(PORT, channelFuture -> {
                serverCond.countDown();
            }));
        }
    }

    @Before
    public void before() throws InterruptedException {
        if (HOST == "127.0.0.1") {
            Assert.assertTrue(serverCond.await(WAIT_TIME, TimeUnit.SECONDS));
        }

        SocketClient socketClient = new SocketClient(PORT, HOST, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                LOGGER.debug("read: {}", msg);
                finishCond.countDown();
            }
        }, channelFuture -> {
            this.channel = channelFuture.channel();
            clientCond.countDown();
        });

        Assert.assertNotNull(socketClient);
        executor.submit(socketClient);

        Assert.assertTrue(clientCond.await(WAIT_TIME, TimeUnit.SECONDS));
    }

    @Test
    public void test() throws InterruptedException {
        L1BeanBuilder builder = new L1Bean.L1BeanBuilder().withVersion(0x01)
                .withCrc(0x01)
                .withSrcAddr(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06})
                .withDstAddr(new byte[]{0x06, 0x05, 0x04, 0x03, 0x02, 0x01})
                .withTimestamp(1L)
                .withSeq(0x01)
                .withCategory(0x02)
                .withTag(0x03)
                .withLast(0x04)
                .withLength(0x09);
        byte[] data = new byte[builder.length];
        channel.writeAndFlush(builder.build());

        Assert.assertTrue(finishCond.await(WAIT_TIME, TimeUnit.SECONDS));

        System.out.println("finish");
    }

    @After
    public void after() {
        Assert.assertNotNull(channel);
        Assert.assertTrue(channel.isOpen());
        channel.disconnect();
    }

    @AfterClass
    public static void afterClass() {
        Assert.assertNotNull(executor);
        Assert.assertFalse(executor.isShutdown());
        executor.shutdown();
    }

}
