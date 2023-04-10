package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.UserEntity;
import me.krzysztofprogramming.userservice.users.models.WrongCredentialsException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final UserEntityRepository userEntityRepository = mock(UserEntityRepository.class);
    private final PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
    private final UserService userService = new UserService(userEntityRepository, passwordEncoder);

    @Test
    public void shouldValidateValidPassword() {
        //give
        when(userEntityRepository.findByUsername(USERNAME)).thenReturn(Optional.of(
                UserEntity.builder()
                        .id(1L)
                        .username(USERNAME)
                        .email("email")
                        .hashedPassword(passwordEncoder.encode(PASSWORD))
                        .build()
        ));

        //when
        userService.validateUserCredentials(USERNAME, PASSWORD);
    }

    @Test
    public void shouldThrowWhenInvalidPassword() {
        //give
        when(userEntityRepository.findByUsername(USERNAME)).thenReturn(Optional.of(
                UserEntity.builder()
                        .id(1L)
                        .username(USERNAME)
                        .email("email")
                        .hashedPassword(passwordEncoder.encode(PASSWORD))
                        .build()
        ));

        //when
        assertThatThrownBy(() -> userService.validateUserCredentials(USERNAME, PASSWORD + "invalid"))
                .isInstanceOf(WrongCredentialsException.class);

    }
}
