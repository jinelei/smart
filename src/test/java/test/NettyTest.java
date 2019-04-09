package test;


import cn.jinelei.rainbow.smart.client.SocketClient;
import cn.jinelei.rainbow.smart.server.SocketServer;
import org.junit.Test;

public class NettyTest {

    @Test
    public void testClient() throws InterruptedException {
        new Thread(new SocketServer(8000)).start();
        Thread t2 = new Thread(new SocketClient(8000));
        t2.start();
        Thread.sleep(20000);
    }
}
