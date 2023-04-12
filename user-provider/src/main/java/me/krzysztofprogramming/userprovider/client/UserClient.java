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
    @Headers("api-key: {key}")
    CustomUserModel getUserById(@Param("id") String id, @Param("key") String key);

    @RequestLine("GET /users/search/findByUsername?username={username}")
    @Headers("api-key: {key}")
    CustomUserModel searchUserByUsername(@Param("username") String username, @Param("key") String key);

    @RequestLine("GET /users/search/findByEmail?email={email}")
    @Headers("api-key: {key}")
    CustomUserModel searchUserByEmail(@Param("email") String email, @Param("key") String key);

    @RequestLine("GET /users")
    @Headers("api-key: {key}")
    GetUsersResponseDto getUsers(@QueryMap Map<String, String> params, @Param("key") String key);

    @RequestLine("PATCH /users/{id}")
    @Headers({"Content-Type: application/json", "api-key: {key}"})
    CustomUserModel updateUser(@Param("id") String userId, CustomUserModel modifiedUser, @Param("key") String key);

    @RequestLine("PATCH /users/{id}")
    @Headers({"Content-Type: application/json", "api-key: {key}"})
    CustomUserModel updateUser(@Param("id") String userId, RequestBody requestBody, @Param("key") String key);

    @RequestLine("POST /validate")
    @Headers({"Content-Type: application/json", "api-key: {key}"})
    void validateUserCredentials(UserCredentialsDto userCredentialsDto, @Param("key") String key);
}
