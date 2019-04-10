package cn.jinelei.rainbow.smart;

import cn.jinelei.rainbow.smart.server.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService service = Executors.newFixedThreadPool(1);
        SocketServer socketServer = new SocketServer(8000);
        service.submit(socketServer);
        synchronized (socketServer) {
            socketServer.wait();
        }
        LOGGER.debug("start SocketServer on {}, pid: {}", 8000, socketServer.pid);
    }
}
