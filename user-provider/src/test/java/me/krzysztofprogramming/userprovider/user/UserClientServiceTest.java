package me.krzysztofprogramming.userprovider.user;

import feign.FeignException;
import okhttp3.RequestBody;
import okio.Buffer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.component.ComponentModel;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

import static me.krzysztofprogramming.userprovider.TestUtils.SINGLE_USER_RESPONSE_DTO;
import static me.krzysztofprogramming.userprovider.TestUtils.USER_MODEL;
import static org.mockito.Mockito.*;

public class UserClientServiceTest {
    private final ComponentModel componentModel = mock(ComponentModel.class);
    private final UserClient userClient = mock(UserClient.class);
    private UserClientService userClientService;

    private final String apiKey = "apiKey";

    @BeforeEach
    void setupUserService() throws NoSuchFieldException, IllegalAccessException {
        when(componentModel.get(CustomUserStorageProviderFactory.URL)).thenReturn("url");
        when(componentModel.get(CustomUserStorageProviderFactory.API_KEY)).thenReturn(apiKey);
        userClientService = new UserClientService(componentModel);
        Field field = UserClientService.class.getDeclaredField("userClient");
        field.setAccessible(true);
        field.set(userClientService, userClient);
        field.setAccessible(false);
    }

    @Test
    public void shouldFindUserById() {
        //given
        when(userClient.getUserById(USER_MODEL.getId(), apiKey)).thenReturn(SINGLE_USER_RESPONSE_DTO);

        //when
        Optional<SingleUserResponseDto> customUserModel = userClientService.findUserById(USER_MODEL.getId());

        //then
        verify(userClient).getUserById(USER_MODEL.getId(), apiKey);
        Assertions.assertThat(customUserModel)
                .isPresent().get()
                .isEqualTo(SINGLE_USER_RESPONSE_DTO);
    }

    @Test
    public void shouldFindUserByEmail() {
        //given
        when(userClient.searchUserByEmail(USER_MODEL.getEmail(), apiKey)).thenReturn(SINGLE_USER_RESPONSE_DTO);

        //when
        Optional<SingleUserResponseDto> customUserModel = userClientService.findUserByEmail(USER_MODEL.getEmail());

        //then
        verify(userClient).searchUserByEmail(USER_MODEL.getEmail(), apiKey);
        Assertions.assertThat(customUserModel)
                .isPresent().get()
                .isEqualTo(SINGLE_USER_RESPONSE_DTO);
    }

    @Test
    public void shouldFindUserByUsername() {
        //given
        when(userClient.searchUserByUsername(USER_MODEL.getUsername(), apiKey)).thenReturn(SINGLE_USER_RESPONSE_DTO);

        //when
        Optional<SingleUserResponseDto> customUserModel = userClientService.findUserByUsername(USER_MODEL.getUsername());

        //then
        verify(userClient).searchUserByUsername(USER_MODEL.getUsername(), apiKey);
        Assertions.assertThat(customUserModel)
                .isPresent().get()
                .isEqualTo(SINGLE_USER_RESPONSE_DTO);
    }

    @Test
    public void shouldReturnEmptyById() {
        //given
        when(userClient.getUserById(any(), eq(apiKey))).thenThrow(FeignException.NotFound.class);

        //when
        Optional<SingleUserResponseDto> customUserModel = userClientService.findUserById(USER_MODEL.getId());

        //then
        verify(userClient).getUserById(USER_MODEL.getId(), apiKey);
        Assertions.assertThat(customUserModel).isEmpty();
    }

    @Test
    public void shouldReturnEmptyByUsername() {
        //given
        when(userClient.searchUserByUsername(any(), eq(apiKey))).thenThrow(FeignException.NotFound.class);

        //when
        Optional<SingleUserResponseDto> customUserModel = userClientService.findUserByUsername(USER_MODEL.getUsername());

        //then
        verify(userClient).searchUserByUsername(USER_MODEL.getUsername(), apiKey);
        Assertions.assertThat(customUserModel).isEmpty();
    }

    @Test
    public void shouldReturnEmptyByEmail() {
        //given
        when(userClient.searchUserByEmail(any(), eq(apiKey))).thenThrow(FeignException.NotFound.class);

        //when
        Optional<SingleUserResponseDto> customUserModel = userClientService.findUserByEmail(USER_MODEL.getEmail());

        //then
        verify(userClient).searchUserByEmail(USER_MODEL.getEmail(), apiKey);
        Assertions.assertThat(customUserModel).isEmpty();
    }

    @Test
    public void shouldRemoveProperty() throws IOException {
        //given
        CustomUserModel userModel = createUserModel();
        when(userClient.updateUser(eq(userModel.getId()), (RequestBody) any(), eq(apiKey))).thenReturn(userModel);
        ArgumentCaptor<RequestBody> bodyArgumentCaptor = ArgumentCaptor.forClass(RequestBody.class);

        //when
        userClientService.removeProperty(userModel.getId(), "lastname");

        //then
        verify(userClient).updateUser(eq(userModel.getId()), bodyArgumentCaptor.capture(), eq(apiKey));
        RequestBody body = bodyArgumentCaptor.getValue();
        Buffer buffer = new Buffer();
        body.writeTo(buffer);

        Assertions.assertThat(buffer.readUtf8())
                .isEqualTo("{\"lastname\": null}");
    }

    private CustomUserModel createUserModel() {
        return CustomUserModel.builder()
                .id("2")
                .username("username")
                .email("email")
                .isEnabled(true)
                .build();
    }

    //TODO test other methods
}
