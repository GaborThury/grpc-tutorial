package com.shopping.service;

import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = getUser(request);
        UserResponse userResponse = buildUserResponse(user);
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    private User getUser(UserRequest request) {
        return new UserDao().getDetails(request.getUsername());
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse
                .newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()))
                .build();
    }
}
