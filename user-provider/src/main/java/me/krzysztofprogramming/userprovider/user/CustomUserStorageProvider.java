package me.krzysztofprogramming.userprovider.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userprovider.client.GetUsersResponseDto;
import me.krzysztofprogramming.userprovider.client.UserClientService;
import me.krzysztofprogramming.userprovider.client.model.SingleUserResponseDto;
import me.krzysztofprogramming.userprovider.roles.RolesManager;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
public class CustomUserStorageProvider implements UserStorageProvider,
        UserLookupProvider, CredentialInputValidator, UserQueryProvider {

    private final Map<String, UserModel> loadedUsersByIdMap = new HashMap<>();
    private final Map<String, UserModel> loadedUsersByEmailMap = new HashMap<>();
    private final Map<String, UserModel> loadedUsersByUsernameMap = new HashMap<>();
    private KeycloakSession keycloakSession;
    private ComponentModel componentModel;
    private UserClientService userClientService;
    private RolesManager rolesManager;

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(RealmModel realm, String rawId) {
        String userId = StorageId.externalId(rawId);
        return Optional.ofNullable(loadedUsersByIdMap.get(userId))
                .orElseGet(
                        () -> userClientService.findUserById(userId)
                                .map(user -> mapToUserAdapter(user, realm))
                                .map(this::addToCacheMaps)
                                .orElse(null)
                );
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        return Optional.ofNullable(loadedUsersByUsernameMap.get(username))
                .orElseGet(
                        () -> userClientService.findUserByUsername(username)
                                .map(user -> mapToUserAdapter(user, realm))
                                .map(this::addToCacheMaps)
                                .orElse(null)
                );
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return Optional.ofNullable(loadedUsersByEmailMap.get(email))
                .orElseGet(
                        () -> userClientService.findUserByEmail(email)
                                .map(user -> mapToUserAdapter(user, realm))
                                .map(this::addToCacheMaps)
                                .orElse(null)
                );
    }

    private CustomUserAdapter mapToUserAdapter(SingleUserResponseDto user, RealmModel realm) {
        rolesManager.addRoles(user.getAssociatedRoles());
        return new CustomUserAdapter(keycloakSession, realm, componentModel, user.getCustomUserModel(),
                rolesManager, userClientService);
    }

    public UserModel addToCacheMaps(UserModel userModel) {
        if (userModel == null) return null;
        log.debug("caching user {}", userModel);
        loadedUsersByIdMap.put(StorageId.externalId(userModel.getId()), userModel);
        loadedUsersByEmailMap.put(userModel.getEmail(), userModel);
        loadedUsersByUsernameMap.put(userModel.getUsername(), userModel);
        return userModel;
    }

    public Stream<UserModel> addToCacheMaps(Stream<UserModel> userModel) {
        userModel.forEach(this::addToCacheMaps);
        return loadedUsersByIdMap.values().stream();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        if (firstResult == null) firstResult = 0;
        if (maxResults == null) maxResults = 100;
        int pageSize = maxResults - firstResult;
        int pageNumber = firstResult / pageSize;

        GetUsersResponseDto usersResponseDto = userClientService.getUsersResponseDto(pageNumber, pageSize);
        rolesManager.addRoles(usersResponseDto.getUsersAssociatedRoles());
        return addToCacheMaps(usersResponseDto.get_embedded().getUser_table().stream()
                .map(user -> new CustomUserAdapter(keycloakSession, realm, componentModel, user,
                        rolesManager, userClientService)));
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        return addToCacheMaps(searchForUserStream(realm, "", firstResult, maxResults));
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        return userClientService.validateCredentials(user.getUsername(), input.getChallengeResponse());
    }

    @Override
    public int getUsersCount(RealmModel realm, boolean includeServiceAccount) {
        if (userClientService == null) return 0;
        return userClientService.countUsers();
    }
}
