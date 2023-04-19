package me.krzysztofprogramming.userprovider.roles;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

interface RoleClient {
    @RequestLine("GET /roles/byId/{id}")
    @Headers("api-key: {key}")
    SingleRoleResponseDto getRole(@Param("id") String roleName, @Param("key") String key);

    @RequestLine("GET /roles")
    @Headers("api-key: {key}")
    GetRolesResponseDto getRoles(@QueryMap Map<String, String> params, @Param("key") String key);
}
