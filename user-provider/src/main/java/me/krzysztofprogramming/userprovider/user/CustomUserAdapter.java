package me.krzysztofprogramming.userprovider.user;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userprovider.client.UserClientService;
import me.krzysztofprogramming.userprovider.roles.RolesManager;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.*;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@ToString(onlyExplicitlyIncluded = true)
class CustomUserAdapter extends AbstractUserAdapter.Streams {

    @ToString.Include
    private final String keycloakId;
    private final UserClientService userClientService;
    private final Map<String, Consumer<String>> propertiesSettersMap = new HashMap<>();
    private final Map<String, String> propertiesNamesMap = new HashMap<>();

    private final RolesManager rolesManager;
    @ToString.Include
    private CustomUserModel user;

    public CustomUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel,
                             CustomUserModel user, RolesManager rolesManager, UserClientService userClientService) {
        super(session, realm, storageProviderModel);
        this.keycloakId = StorageId.keycloakId(storageProviderModel, user.getId());
        this.user = user;
        this.rolesManager = rolesManager;
        this.userClientService = userClientService;
        this.initMaps();
    }

    private void initMaps() {
        propertiesSettersMap.put(FIRST_NAME, this::setFirstName);
        propertiesSettersMap.put(LAST_NAME, this::setLastName);
        propertiesSettersMap.put(USERNAME, this::setUsername);
        propertiesSettersMap.put(EMAIL, this::setEmail);
        propertiesSettersMap.put(ENABLED, this::setEnabled);

        propertiesNamesMap.put(USERNAME, "username");
        propertiesNamesMap.put(FIRST_NAME, "firstname");
        propertiesNamesMap.put(LAST_NAME, "lastname");
        propertiesNamesMap.put(ENABLED, "isEnabled");
        propertiesNamesMap.put(EMAIL, "email");
    }

    @Override
    public List<String> getAttribute(String name) {
        return getAttributes().getOrDefault(name, List.of());
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return getAttribute(name).stream();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        attributes.add(UserModel.FIRST_NAME, getFirstName());
        attributes.add(UserModel.LAST_NAME, getLastName());
        attributes.add(UserModel.ENABLED, user.getIsEnabled() + "");
        return attributes;
    }

    @Override
    public String getFirstAttribute(String name) {
        List<String> attribute = getAttribute(name);
        return attribute.size() > 0 ? attribute.get(0) : null;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        user = userClientService.updateUser(user.getId(), CustomUserModel.builder().username(username).build());
    }

    @Override
    public Long getCreatedTimestamp() {
        return user.getCreatedDate().getTime();
    }

    @Override
    public String getFirstName() {
        return user.getFirstname();
    }

    @Override
    public void setFirstName(String firstName) {
        user = userClientService.updateUser(user.getId(), CustomUserModel.builder().firstname(firstName).build());
    }

    @Override
    public String getLastName() {
        return user.getLastname();
    }

    @Override
    public void setLastName(String lastName) {
        user = userClientService.updateUser(user.getId(), CustomUserModel.builder().lastname(lastName).build());
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        user = userClientService.updateUser(user.getId(), CustomUserModel.builder().email(email).build());
    }

    @Override
    public boolean isEnabled() {
        return user.getIsEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        user = userClientService.updateUser(user.getId(), CustomUserModel.builder().isEnabled(enabled).build());
    }

    private void setEnabled(String value) {
        setEnabled(Boolean.parseBoolean(value));
    }

    @Override
    public String getId() {
        return this.keycloakId;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        Optional.ofNullable(this.propertiesSettersMap.get(name))
                .orElseThrow(() -> new ReadOnlyException("Can't update this property"))
                .accept(value);
    }

    @Override
    public void removeAttribute(String name) {
        if (!propertiesNamesMap.containsKey(name)) throw new ReadOnlyException("Can't update this property");
        user = userClientService.removeProperty(user.getId(), Optional.ofNullable(propertiesNamesMap.get(name))
                .orElseThrow(() -> new ReadOnlyException("Can't remove this property")));
    }

    @Override
    public Set<RoleModel> getRealmRoleMappings() {
        return super.getRealmRoleMappings();
    }

    @Override
    public Stream<RoleModel> getRealmRoleMappingsStream() {
        return super.getRealmRoleMappingsStream();
    }

    @Override
    public Set<RoleModel> getClientRoleMappings(ClientModel app) {
        return super.getClientRoleMappings(app);
    }

    @Override
    public Stream<RoleModel> getClientRoleMappingsStream(ClientModel app) {
        return super.getClientRoleMappingsStream(app);
    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        return getClientRoleMappingsStream(null).collect(Collectors.toSet());
    }

    @Override
    public Stream<RoleModel> getRoleMappingsStream() {
        return user.getUserRolesIds().stream().map(rolesManager::getRoleById);
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return user.getUserRolesIds().contains(role.getId());
    }

    @Override
    public void grantRole(RoleModel role) {
        super.grantRole(role);
    }

    @Override
    public void deleteRoleMapping(RoleModel role) {
        super.deleteRoleMapping(role);
    }

    @Override
    public void removeRequiredAction(String action) {
    }

    @Override
    public void removeRequiredAction(RequiredAction action) {
    }

    @Override
    public void setEmailVerified(boolean verified) {
        if (verified) super.setEmailVerified(true);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        setSingleAttribute(name, values.get(0));
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(session, realm, this);
    }


}
