package me.krzysztofprogramming.userprovider;

import me.krzysztofprogramming.userprovider.user.CustomUserModel;

import java.time.Instant;
import java.util.Date;

public class TestUtils {

    public final static Date DATE = Date.from(Instant.parse("2022-04-01T12:34:56.789Z"));
    public static final CustomUserModel USER_MODEL = CustomUserModel.builder()
            .id("1")
            .createdDate(DATE)
            .lastModifiedDate(DATE)
            .firstname("firstname")
            .username("username")
            .lastname("lastname")
            .email("email")
            .isEnabled(true)
            .build();
}
