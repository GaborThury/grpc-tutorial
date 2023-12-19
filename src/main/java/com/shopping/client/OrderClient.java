package com.shopping.client;

import com.shopping.stubs.order.Order;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.Channel;

import java.util.List;

public class OrderClient {
    private final OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    public OrderClient(Channel channel) {
        this.orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public List<Order> getOrders(int userId) {
        OrderRequest orderRequest = buildRequest(userId);
        OrderResponse orderResponse = orderServiceBlockingStub.getOrdersForUser(orderRequest);

        return orderResponse.getOrderList();
    }

    private OrderRequest buildRequest(int userId) {
        return OrderRequest
                .newBuilder()
                .setUserId(userId)
                .build();
    }
}
