package com.shopping.server;

import com.shopping.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServer {

    private static final Logger LOGGER = Logger.getLogger(UserServer.class.getName());
    private static final int PORT_NUMBER = 50051;
    private static final int TERMINATION_TIMEOUT = 30;

    private Server server;

    public static void main(String[] args) throws InterruptedException {
        UserServer userServer = new UserServer();
        userServer.startServer();
        userServer.blockUntilShutdown();
    }

    public void startServer() {
        try {
            server = getServer();
            addShutdownHook();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server could not start", e);
        }
    }

    public void stopServer() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private Server getServer() throws IOException {
        return ServerBuilder
                .forPort(PORT_NUMBER)
                .addService(new UserServiceImpl())
                .build()
                .start();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Clean server shutdown in case JVM was shut down");
            try {
                UserServer.this.stopServer();
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Server shutdown", e);
            }
        }));
    }


}
