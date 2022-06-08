package com.guinetik.challenge.remote;

import com.guinetik.challenge.model.User;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface RemoteUserApi extends RemoteApi {

    @RequestLine("GET /users")
    List<User> getAllUsers();

    @RequestLine("GET /users/{userId}")
    User getUserById(@Param("userId") String userId);
}
