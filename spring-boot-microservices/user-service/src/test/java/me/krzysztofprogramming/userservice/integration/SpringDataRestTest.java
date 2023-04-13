package me.krzysztofprogramming.userservice.integration;


import me.krzysztofprogramming.userservice.users.UserEntityRepository;
import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpringDataRestTest {


    private static final Date DATE = Date.from(Instant.parse("2022-04-01T12:34:56.789Z"));
    private final UserEntity expectedUser = createUserEntity();
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler handler;

    @Value("${application.api-key.value}")
    private String apiKey;

    @Value("${application.api-key.header}")
    private String apiKeyHeader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    public void shouldAddNewUser() {

        //given

        NewUserRequest newUserRequest = NewUserRequest.builder()
                .username(expectedUser.getUsername())
                .password("password")
                .firstname(expectedUser.getFirstname())
                .email(expectedUser.getEmail())
                .build();

        ZonedDateTime createdDateTime = createZonedDateTime("2020-10-17 00:00 +0200");
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(createdDateTime.toLocalDateTime()));

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(apiKeyHeader, apiKey)
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
                .jsonPath("$.createdDate").isEqualTo("2020-10-16T22:00:00.000+00:00")
                .jsonPath("$.lastModifiedDate").isEqualTo("2020-10-16T22:00:00.000+00:00")
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
                .returns(Timestamp.from(createdDateTime.toInstant()), UserEntity::getCreatedDate)
                .returns(Timestamp.from(createdDateTime.toInstant()), UserEntity::getLastModifiedDate)
                .matches(userEntity -> passwordEncoder.matches("password", userEntity.getHashedPassword()));
    }

    @Test
    public void shouldReturn403WhenWrongApiKey() {
        //given
        webTestClient.get().uri("/users")
                .header(apiKeyHeader, apiKey + "wrong")
                .accept(MediaType.APPLICATION_JSON)
                //when
                .exchange()
                .expectStatus().isForbidden();
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

    private ZonedDateTime createZonedDateTime(String date) {
        return ZonedDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm X"));
    }

}
