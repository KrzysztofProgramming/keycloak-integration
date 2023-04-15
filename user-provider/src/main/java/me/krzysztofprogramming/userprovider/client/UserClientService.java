package me.krzysztofprogramming.userprovider.client;

import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userprovider.client.model.SingleUserResponseDto;
import me.krzysztofprogramming.userprovider.user.CustomUserModel;
import me.krzysztofprogramming.userprovider.user.CustomUserStorageProviderFactory;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.keycloak.component.ComponentModel;
import org.keycloak.storage.ReadOnlyException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class UserClientService {
    private final UserClient userClient;

    private final ComponentModel componentModel;
    private final Map<Class<? extends Throwable>, String> exceptionMessagesMap = new HashMap<>();

    public UserClientService(ComponentModel model) {
        userClient = createUserClient(model);
        this.componentModel = model;

        exceptionMessagesMap.put(FeignException.NotFound.class, "User does not exists");
        exceptionMessagesMap.put(FeignException.Conflict.class, "This property cannot have that value");
    }

    private UserClient createUserClient(ComponentModel model) {
        return Feign.builder().
                client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new UserDecoder())
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.FULL)
                .target(UserClient.class, model.get(CustomUserStorageProviderFactory.URL));
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
        Map<String, String> params = new HashMap<>();
        params.put("page", pageNumber + "");
        params.put("pageSize", pageSize + "");
        return catchErrors(
                () -> userClient.getUsers(params, getApiKey()),
                e -> new GetUsersResponseDto(new GetUsersResponseListDto(Collections.emptyList()),
                        Collections.emptySet(), new PageResponseDto(0, pageSize, 0, pageNumber))
        );
    }

    public boolean validateCredentials(String username, String password) {
        return catchErrors(
                () -> {
                    userClient.validateUserCredentials(new UserCredentialsDto(username, password), getApiKey());
                    return true;
                },
                e -> false
        );
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

    private String getApiKey() {
        return componentModel.get(CustomUserStorageProviderFactory.API_KEY);
    }

    private <T> T catchErrors(Supplier<T> action, Function<FeignException, T> onErrorSupplier) {
        try {
            return action.get();
        } catch (FeignException.NotFound | FeignException.Conflict | FeignException.Forbidden
                 | FeignException.Unauthorized exception) {
            if (exception instanceof FeignException.Forbidden
                    || exception instanceof FeignException.Unauthorized) {
                log.warn("Cannot access user-service, make sure you setup api-key correctly");
            }
            return onErrorSupplier.apply(exception);
        }
    }

    private <T> Optional<T> catchErrors(Supplier<Optional<T>> action) {
        return catchErrors(action, e -> Optional.empty());
    }
}
