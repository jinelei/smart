package test;


import cn.jinelei.rainbow.smart.client.NettyClient;
import cn.jinelei.rainbow.smart.server.NettyServer;
import org.junit.Test;

public class NettyTest {

    @Test
    public void testClient() throws InterruptedException {
        new Thread(new NettyServer(8000)).start();
        Thread t2 = new Thread(new NettyClient(8000));
        t2.start();
        Thread.sleep(20000);
    }
}
