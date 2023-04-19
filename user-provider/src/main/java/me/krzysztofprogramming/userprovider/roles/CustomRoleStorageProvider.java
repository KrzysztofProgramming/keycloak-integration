package me.krzysztofprogramming.userprovider.roles;

import lombok.AllArgsConstructor;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.role.RoleStorageProvider;

import java.util.Set;
import java.util.stream.Stream;

@AllArgsConstructor
public class CustomRoleStorageProvider implements RoleStorageProvider {

    public static final String PROVIDER_ID = "custom-role-provider";
    private final RolesCacheStorage rolesCacheStorage;
    private final RoleClientService roleClientService;

    @Override
    public void close() {

    }

    @Override
    public RoleModel getRealmRole(RealmModel realm, String name) {
        return rolesCacheStorage.getRoleByName(name, realm).orElseGet(
                () -> roleClientService.getRoleByName(name)
                        .map(role -> cacheRole(role, realm))
                        .orElse(null)
        );
    }

    @Override
    public RoleModel getRoleById(RealmModel realm, String id) {
        return getRealmRole(realm, id);
    }

    private CustomRoleModel cacheRole(SingleRoleResponseDto role, RealmModel realmModel) {
        if (role == null) return null;
        rolesCacheStorage.addRole(role);
        return rolesCacheStorage.getRoleByName(role.getRoleResponseDto().getName(), realmModel)
                .orElse(null);
    }

    public void cacheRoles(Set<RoleResponseDto> roleResponseDtos) {
        rolesCacheStorage.addRoles(roleResponseDtos);
    }

    @Override
    public Stream<RoleModel> searchForRolesStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        if (firstResult == null) firstResult = 0;
        if (maxResults == null) maxResults = 100;
        int pageSize = maxResults - firstResult;
        int pageNumber = firstResult / pageSize;

        GetRolesResponseDto responseDto = roleClientService.getRoles(pageNumber, pageSize);
        Set<RoleResponseDto> resultRoles = responseDto.get_embedded().getRole_table();
        Set<RoleResponseDto> allRoles = responseDto.getAssociatedRoles();

        allRoles.addAll(resultRoles);
        rolesCacheStorage.addRoles(allRoles);

        return resultRoles.stream()
                .map(roleResponseDto -> rolesCacheStorage
                        .getRoleByName(roleResponseDto.getName(), realm).orElse(null));
    }

    @Override
    public RoleModel getClientRole(ClientModel client, String name) {
        return null;
    }

    @Override
    public Stream<RoleModel> searchForClientRolesStream(ClientModel client, String search, Integer first, Integer max) {
        return Stream.empty();
    }
}
