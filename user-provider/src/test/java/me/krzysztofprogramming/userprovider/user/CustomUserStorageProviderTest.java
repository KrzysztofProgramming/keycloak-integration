package me.krzysztofprogramming.userprovider.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static me.krzysztofprogramming.userprovider.TestUtils.SINGLE_USER_RESPONSE_DTO;
import static me.krzysztofprogramming.userprovider.TestUtils.USER_MODEL;
import static org.mockito.Mockito.*;

public class CustomUserStorageProviderTest {

    private final KeycloakSession keycloakSession = mock(KeycloakSession.class);
    private final ComponentModel componentModel = mock(ComponentModel.class);
    private final UserClientService userClientService = mock(UserClientService.class);
    private final RealmModel realmModel = mock(RealmModel.class);

    private final CustomUserStorageProvider userProvider =
            new CustomUserStorageProvider(keycloakSession, componentModel, userClientService);


    @BeforeEach
    void setupMocks() {
        when(componentModel.getId()).thenReturn("componentId");
    }

    @Test
    public void shouldCacheUser() {

        //given
        when(userClientService.findUserById(USER_MODEL.getId())).thenReturn(Optional.of(SINGLE_USER_RESPONSE_DTO));
        when(userClientService.findUserByUsername(USER_MODEL.getUsername())).thenReturn(Optional.of(SINGLE_USER_RESPONSE_DTO));
        when(userClientService.findUserByEmail(USER_MODEL.getEmail())).thenReturn(Optional.of(SINGLE_USER_RESPONSE_DTO));
        List<UserModel> userModelList = new LinkedList<>();

        //when
        UserModel reference = userProvider.getUserById(realmModel, StorageId.keycloakId(componentModel, USER_MODEL.getId()));
        userModelList.add(userProvider.getUserById(realmModel, StorageId.keycloakId(componentModel, USER_MODEL.getId())));
        userModelList.add(userProvider.getUserByUsername(realmModel, USER_MODEL.getUsername()));
        userModelList.add(userProvider.getUserByEmail(realmModel, USER_MODEL.getEmail()));

        //then
        verify(userClientService).findUserById(USER_MODEL.getId());
        verifyNoMoreInteractions(userClientService);
        Assertions.assertThat(userModelList.stream().map(reference::equals).reduce(true, (a, b) -> a && b)).isTrue();
    }
}
