package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
    private static final String LOCALHOST_50052 = "localhost:50052";
    private static final int TERMINATION_TIMEOUT = 5;

    private final UserDao userDao = new UserDao();

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = getUser(request);
        List<Order> orders = getOrders(user.getId());
        UserResponse userResponse = buildUserResponse(user, orders.size());

        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(int userId) {
        OrderClient orderClient = getOrderClient();
        return orderClient.getOrders(userId);
    }

    private OrderClient getOrderClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(LOCALHOST_50052)
                .usePlaintext()
                .build();
        try {
            channel.shutdown().awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Channel did not shut down", e);
        }
        return new OrderClient(channel);
    }

    private User getUser(UserRequest request) {
        return userDao.getDetails(request.getUsername());
    }

    private UserResponse buildUserResponse(User user, int size) {
        return UserResponse
                .newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()))
                .setNoOfOrders(size)
                .build();
    }
}
