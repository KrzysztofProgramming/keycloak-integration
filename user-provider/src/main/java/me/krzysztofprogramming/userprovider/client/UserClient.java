package me.krzysztofprogramming.userprovider.client;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import me.krzysztofprogramming.userprovider.user.CustomUserModel;
import okhttp3.RequestBody;

import java.util.Map;

interface UserClient {
    @RequestLine("GET /users/{id}")
    CustomUserModel getUserById(@Param("id") String id);

    @RequestLine("GET /users/search/findByUsername?username={username}")
    CustomUserModel searchUserByUsername(@Param("username") String username);

    @RequestLine("GET /users/search/findByEmail?email={email}")
    CustomUserModel searchUserByEmail(@Param("email") String email);

    @RequestLine("GET /users")
    GetUsersResponseDto getUsers(@QueryMap Map<String, String> params);

    @RequestLine("PATCH /users/{id}")
    @Headers("Content-Type: application/json")
    CustomUserModel updateUser(@Param("id") String userId, CustomUserModel modifiedUser);

    @RequestLine("PATCH /users/{id}")
    @Headers("Content-Type: application/json")
    CustomUserModel updateUser(@Param("id") String userId, RequestBody requestBody);

    @RequestLine("POST /validate")
    @Headers("Content-Type: application/json")
    void validateUserCredentials(UserCredentialsDto userCredentialsDto);
}
