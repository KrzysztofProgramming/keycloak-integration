package me.krzysztofprogramming.userprovider;

import me.krzysztofprogramming.userprovider.roles.RoleResponseDto;
import me.krzysztofprogramming.userprovider.user.CustomUserModel;
import me.krzysztofprogramming.userprovider.user.SingleUserResponseDto;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class TestUtils {

    public final static Date DATE = Date.from(Instant.parse("2022-04-01T12:34:56.789Z"));
    public static final CustomUserModel USER_MODEL = CustomUserModel.builder()
            .id("1")
            .createdDate(DATE)
            .lastModifiedDate(DATE)
            .firstname("firstname")
            .username("username")
            .lastname("lastname")
            .userRolesIds(Set.of("A", "C"))
            .email("email")
            .isEnabled(true)
            .build();

    public static final Set<RoleResponseDto> ASSOCIATED_ROLES = Set.of(
            new RoleResponseDto("A", "", Set.of("B")),
            new RoleResponseDto("B", "", Set.of("A")),
            new RoleResponseDto("C", "", Collections.emptySet())
    );

    public static final SingleUserResponseDto SINGLE_USER_RESPONSE_DTO =
            new SingleUserResponseDto(USER_MODEL, ASSOCIATED_ROLES);
}
