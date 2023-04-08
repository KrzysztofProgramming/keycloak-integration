package me.krzysztofprogramming.userprovider.user;

import me.krzysztofprogramming.userprovider.client.UserClientService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;
import java.util.stream.Stream;

import static me.krzysztofprogramming.userprovider.TestUtils.DATE;
import static me.krzysztofprogramming.userprovider.TestUtils.USER_MODEL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class CustomUserAdapterTest {


    private final CustomUserModel userModel = USER_MODEL;

    private final CustomUserModel expectedUserModel = CustomUserModel.builder()
            .id("1")
            .createdDate(DATE)
            .lastModifiedDate(DATE)
            .firstname("value")
            .username("value")
            .lastname("value")
            .email("value")
            .isEnabled(false)
            .build();

    private final UserClientService userService = mock(UserClientService.class);
    private final KeycloakSession keycloakSession = mock(KeycloakSession.class);
    private final RealmModel realmModel = mock(RealmModel.class);
    private final ComponentModel componentModel = mock(ComponentModel.class);

    private final CustomUserAdapter userAdapter = new CustomUserAdapter(keycloakSession, realmModel, componentModel,
            userModel, userService);

    private static Stream<Arguments> createPropertiesToCheckArguments() {
        return Stream.of(
                Arguments.of(UserModel.USERNAME, "value",
                        CustomUserModel.builder().username("value").build(),
                        (Function<CustomUserAdapter, Object>) CustomUserAdapter::getUsername),
                Arguments.of(UserModel.EMAIL, "value",
                        CustomUserModel.builder().email("value").build(),
                        (Function<CustomUserAdapter, Object>) CustomUserAdapter::getEmail),
                Arguments.of(UserModel.FIRST_NAME, "value",
                        CustomUserModel.builder().firstname("value").build(),
                        (Function<CustomUserAdapter, Object>) CustomUserAdapter::getFirstName),
                Arguments.of(UserModel.LAST_NAME, "value",
                        CustomUserModel.builder().lastname("value").build(),
                        (Function<CustomUserAdapter, Object>) CustomUserAdapter::getLastName),
                Arguments.of(UserModel.ENABLED, false,
                        CustomUserModel.builder().isEnabled(false).build(),
                        (Function<CustomUserAdapter, Object>) CustomUserAdapter::isEnabled)
        );
    }

    @BeforeEach
    void setupMocks() {
        Mockito.when(realmModel.getId()).thenReturn("componentId");
        Mockito.when(userService.updateUser(anyString(), any())).thenReturn(expectedUserModel);
    }

    @ParameterizedTest
    @MethodSource("createPropertiesToCheckArguments")
    public void shouldSetProperty(String propertyName, Object expectedValue, CustomUserModel expectedModel,
                                  Function<CustomUserAdapter, Object> propertyGetter) {
        //when
        userAdapter.setSingleAttribute(propertyName, expectedValue.toString());

        //then
        Mockito.verify(userService).updateUser(userModel.getId(), expectedModel);
        Assertions.assertThat(userAdapter).returns(expectedValue, propertyGetter);

    }
}
