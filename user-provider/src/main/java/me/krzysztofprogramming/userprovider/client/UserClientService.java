package me.krzysztofprogramming.userprovider.client;

import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userprovider.user.CustomUserModel;
import me.krzysztofprogramming.userprovider.user.CustomUserStorageProviderFactory;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.keycloak.component.ComponentModel;

import java.util.*;

@Slf4j
public class UserClientService {
    private final UserClient userClient;

    private final Map<Class<? extends Throwable>, String> exceptionMessagesMap = new HashMap<>();

    public UserClientService(ComponentModel model) {
        userClient = createUserClient(model);

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

    public Optional<CustomUserModel> findUserById(String id) {
        try {
            return Optional.of(userClient.getUserById(id));
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }

    public Optional<CustomUserModel> findUserByEmail(String email) {
        try {
            return Optional.of(userClient.searchUserByEmail(email));
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }

    public Optional<CustomUserModel> findUserByUsername(String username) {
        try {
            return Optional.of(userClient.searchUserByUsername(username));
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<CustomUserModel> findUsers(int pageNumber, int pageSize) {
        return getUsersResponseDto(pageNumber, pageSize).get_embedded().getUser_table();
    }

    public int countUsers() {
        return this.getUsersResponseDto(0, 1).getPage().getTotalElements();
    }

    private GetUsersResponseDto getUsersResponseDto(int pageNumber, int pageSize) {
        Map<String, String> params = new HashMap<>();
        params.put("page", pageNumber + "");
        params.put("pageSize", pageSize + "");
        try {
            return userClient.getUsers(params);
        } catch (FeignException.NotFound exception) {
            return new GetUsersResponseDto(new GetUsersResponseListDto(Collections.emptyList()),
                    new PageResponseDto(0, pageSize, 0, pageNumber));
        }
    }

    public boolean validateCredentials(String username, String password) {
        try {
            userClient.validateUserCredentials(new UserCredentialsDto(username, password));
            return true;
        } catch (FeignException.BadRequest exception) {
            return false;
        }
    }

    public CustomUserModel updateUser(String userId, CustomUserModel modifiedUser) {
        try {
            return this.userClient.updateUser(userId, modifiedUser);
        } catch (FeignException.Conflict | FeignException.NotFound e) {
            throw new RuntimeException("Can't update user: " + exceptionMessagesMap
                    .getOrDefault(e.getClass(), "unknown reason"));
        }
    }

    public CustomUserModel removeProperty(String userId, String propertyName) {
        try {
            String jsonString = "{\"" + propertyName + "\": null}";
            RequestBody body = RequestBody.create(jsonString, MediaType.get("application/json"));
            return this.userClient.updateUser(userId, body);
        } catch (FeignException.Conflict | FeignException.NotFound e) {
            throw new RuntimeException("Can't update user: " + exceptionMessagesMap
                    .getOrDefault(e.getClass(), "unknown reason"));
        }
    }

}