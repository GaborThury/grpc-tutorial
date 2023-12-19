package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final Logger LOGGER = Logger.getLogger(OrderServiceImpl.class.getName());
    private final OrderDao orderDao = new OrderDao();
    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());
        OrderResponse orderResponse = buildOrderResponse(orders);

        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }

    private OrderResponse buildOrderResponse(List<Order> orders) {
        LOGGER.log(Level.INFO, "Received orders from OrderDao. Converting to OrderResponse proto objects...");
        return OrderResponse
                .newBuilder()
                .addAllOrder(mapOrdersToStubs(orders))
                .build();
    }

    private List<com.shopping.stubs.order.Order> mapOrdersToStubs(List<Order> orders) {
        return orders.stream()
                .map(order -> com.shopping.stubs.order.Order
                        .newBuilder()
                        .setUserId(order.getUserId())
                        .setOrderId(order.getOrderId())
                        .setNoOfItems(order.getNoOfItems())
                        .setTotalAmount(order.getTotalAmount())
                        .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
                        .build())
                .toList();
    }
}
