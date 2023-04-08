package me.krzysztofprogramming.userservice.integration;


import me.krzysztofprogramming.userservice.users.UserEntityRepository;
import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpringDataRestTest {


    private static final Date DATE = Date.from(Instant.parse("2022-04-01T12:34:56.789Z"));
    private final UserEntity expectedUser = createUserEntity();
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Order(1)
    public void shouldAddNewUser() {

        //given

        NewUserRequest newUserRequest = NewUserRequest.builder()
                .username(expectedUser.getUsername())
                .password("password")
                .firstname(expectedUser.getFirstname())
                .email(expectedUser.getEmail())
                .build();

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newUserRequest), NewUserRequest.class)

                //when
                .exchange()

                //then
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(expectedUser.getId())
                .jsonPath("$.username").isEqualTo(expectedUser.getUsername())
                .jsonPath("$.email").isEqualTo(expectedUser.getEmail())
                .jsonPath("$.firstname").isEqualTo(expectedUser.getFirstname())
                .jsonPath("$.username").isEqualTo(expectedUser.getUsername())
                .jsonPath("$.isEnabled").isEqualTo(expectedUser.getIsEnabled())
                .jsonPath("$.createdDate").exists()
                .jsonPath("$.lastModifiedDate").exists()
                .jsonPath("$.lastname").doesNotExist()
                .jsonPath("$.hashedPassword").doesNotExist()
                .jsonPath("$.password").doesNotExist();

        Assertions.assertThat(userEntityRepository.findById(1L))
                .isPresent().get()
                .returns(expectedUser.getId(), UserEntity::getId)
                .returns(expectedUser.getEmail(), UserEntity::getEmail)
                .returns(expectedUser.getUsername(), UserEntity::getUsername)
                .returns(expectedUser.getFirstname(), UserEntity::getFirstname)
                .returns(expectedUser.getIsEnabled(), UserEntity::getIsEnabled)
                .returns(expectedUser.getLastname(), UserEntity::getLastname)
                .matches(userEntity -> passwordEncoder.matches("password", userEntity.getHashedPassword()));
    }


    private UserEntity createUserEntity() {
        return UserEntity.builder()
                .id(1L)
                .firstname("firstname")
                .username("username")
                .createdDate(DATE)
                .lastModifiedDate(DATE)
                .email("email@gmail.com")
                .isEnabled(true)
                .build();
    }

}
