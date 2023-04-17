package me.krzysztofprogramming.userprovider;

import me.krzysztofprogramming.userprovider.client.model.SingleRoleResponseDto;
import me.krzysztofprogramming.userprovider.client.model.SingleUserResponseDto;
import me.krzysztofprogramming.userprovider.user.CustomUserModel;

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

    public static final Set<SingleRoleResponseDto> ASSOCIATED_ROLES = Set.of(
            new SingleRoleResponseDto("A", "", Set.of("B")),
            new SingleRoleResponseDto("B", "", Set.of("A")),
            new SingleRoleResponseDto("C", "", Collections.emptySet())
    );

    public static final SingleUserResponseDto SINGLE_USER_RESPONSE_DTO =
            new SingleUserResponseDto(USER_MODEL, ASSOCIATED_ROLES);
}
