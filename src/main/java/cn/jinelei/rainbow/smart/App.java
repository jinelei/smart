package cn.jinelei.rainbow.smart;

import cn.jinelei.rainbow.smart.server.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static int port = 8000;

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.error("NumberFormatException: {}", args[0]);
            }
        }
        ExecutorService service = Executors.newFixedThreadPool(1);
        SocketServer socketServer = new SocketServer(port);
        service.submit(socketServer);
    }
}
