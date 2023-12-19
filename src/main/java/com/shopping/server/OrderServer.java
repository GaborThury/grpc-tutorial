package com.shopping.server;

import com.shopping.service.OrderServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderServer {

    private static final Logger LOGGER = Logger.getLogger(OrderServer.class.getName());
    private static final int PORT_NUMBER = 50052;
    private static final int TERMINATION_TIMEOUT = 30;

    private Server server;

    public static void main(String[] args) throws InterruptedException {
        OrderServer orderServer = new OrderServer();
        orderServer.startServer();
        orderServer.blockUntilShutdown();
    }

    public void startServer() {
        try {
            server = getServer();
            LOGGER.info("Server started on port: " + PORT_NUMBER);
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
                .addService(new OrderServiceImpl())
                .build()
                .start();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Clean server shutdown in case JVM was shut down");
            try {
                OrderServer.this.stopServer();
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Server shutdown interrupted", e);
            }
        }));
    }
}
