package me.krzysztofprogramming.userprovider.user;

import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userprovider.client.AbstractClientService;
import me.krzysztofprogramming.userprovider.client.PageResponseDto;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.keycloak.component.ComponentModel;
import org.keycloak.storage.ReadOnlyException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
class UserClientService extends AbstractClientService {
    private final UserClient userClient;
    private final Map<Class<? extends Throwable>, String> exceptionMessagesMap = new HashMap<>();

    public UserClientService(ComponentModel model) {
        super(model);
        userClient = createUserClient();

        exceptionMessagesMap.put(FeignException.NotFound.class, "User does not exists");
        exceptionMessagesMap.put(FeignException.Conflict.class, "This property cannot have that value");
    }

    private UserClient createUserClient() {
        return Feign.builder().
                client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new UserDecoder())
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.FULL)
                .target(UserClient.class, getComponentModel().get(CustomUserStorageProviderFactory.URL));
    }

    public Optional<SingleUserResponseDto> findUserById(String id) {
        return catchErrors(
                () -> Optional.of(userClient.getUserById(id, getApiKey())),
                e -> Optional.empty()
        );
    }

    public Optional<SingleUserResponseDto> findUserByEmail(String email) {
        return catchErrors(
                () -> Optional.of(userClient.searchUserByEmail(email, getApiKey()))
        );
    }

    public Optional<SingleUserResponseDto> findUserByUsername(String username) {
        return catchErrors(
                () -> Optional.of(userClient.searchUserByUsername(username, getApiKey()))
        );
    }

    public int countUsers() {
        return catchErrors(
                () -> this.getUsersResponseDto(0, 1).getPage().getTotalElements(),
                e -> 0
        );
    }

    public GetUsersResponseDto getUsersResponseDto(int pageNumber, int pageSize) {
        return catchErrors(
                () -> userClient.getUsers(createPageParamsMap(pageNumber, pageSize), getApiKey()),
                e -> new GetUsersResponseDto(new GetUsersResponseListDto(Collections.emptySet()),
                        Collections.emptySet(), new PageResponseDto(0, pageSize, 0, pageNumber))
        );
    }

    public boolean validateCredentials(String username, String password) {
        try {
            userClient.validateUserCredentials(new UserCredentialsDto(username, password), getApiKey());
            return true;
        } catch (FeignException.BadRequest e) {
            return false;
        }
    }

    public CustomUserModel updateUser(String userId, CustomUserModel modifiedUser) {
        return catchErrors(
                () -> this.userClient.updateUser(userId, modifiedUser, getApiKey()),
                e -> {
                    throw new ReadOnlyException("Can't update user: " + exceptionMessagesMap
                            .getOrDefault(e.getClass(), "unknown reason"));
                }
        );
    }

    public CustomUserModel removeProperty(String userId, String propertyName) {
        return catchErrors(
                () -> {
                    String jsonString = "{\"" + propertyName + "\": null}";
                    RequestBody body = RequestBody.create(jsonString, MediaType.get("application/json"));
                    return this.userClient.updateUser(userId, body, getApiKey());
                },
                e -> {
                    throw new ReadOnlyException("Can't update user: " + exceptionMessagesMap
                            .getOrDefault(e.getClass(), "unknown reason"));
                }
        );
    }

    public boolean deleteUser(String userId) {
        return catchErrors(
                () -> {
                    userClient.deleteUser(userId, getApiKey());
                    return true;
                },
                e -> false
        );
    }

}
