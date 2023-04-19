package me.krzysztofprogramming.userprovider.roles;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.role.RoleStorageProviderFactory;

public class CustomRoleStorageProviderFactory implements RoleStorageProviderFactory<CustomRoleStorageProvider> {

    @Override
    public CustomRoleStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new CustomRoleStorageProvider(new RolesCacheStorage(), new RoleClientService(componentModel));
    }

    @Override
    public String getId() {
        return CustomRoleStorageProvider.PROVIDER_ID;
    }
}
