package me.krzysztofprogramming.userprovider.roles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.RealmModel;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class RolesCacheStorage {

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, CustomRoleModel> rolesByNames = new HashMap<>();


    public RolesCacheStorage() {
    }


    public void addRoles(Set<RoleResponseDto> rawRoles) {
        log.debug("Adding new roles {}", rawRoles);
        Map<String, CustomRoleModel> newRoles = rawRoles.stream().map(this::mapToCustomRole)
                .collect(Collectors.toMap(CustomRoleModel::getName, Function.identity()));
        linkRoles(newRoles, rawRoles);
        mergeWithSavedRoles(newRoles);

        log.debug("Roles after adding {}", rolesByNames);
    }

    private CustomRoleModel mapToCustomRole(RoleResponseDto roleResponseDto) {
        return new CustomRoleModel(
                this,
                null,
                roleResponseDto.getName(),
                roleResponseDto.getDescription(),
                Collections.emptySet()
        );
    }

    public void addRole(SingleRoleResponseDto rawRoles) {
        addRoles(Stream.concat(
                Stream.of(rawRoles.getRoleResponseDto()),
                rawRoles.getAssociatedRoles().stream()
        ).collect(Collectors.toSet()));
    }


    private void mergeWithSavedRoles(Map<String, CustomRoleModel> newRoles) {
        if (!rolesByNames.isEmpty()) {
            Iterator<CustomRoleModel> iterator = newRoles.values().iterator();
            while (iterator.hasNext()) {
                CustomRoleModel newRole = iterator.next();
                CustomRoleModel oldRole = rolesByNames.get(newRole.getName());
                if (oldRole == null) continue;

                oldRole.setDescription(newRole.getDescription());
                oldRole.setAssociatedRoles(newRole.getAssociatedRoles());
                iterator.remove();
            }
        }

        rolesByNames.putAll(newRoles);
    }

    private void linkRoles(Map<String, CustomRoleModel> newRoles, Set<RoleResponseDto> rawRoles) {
        rawRoles.forEach(rawRole -> {
            CustomRoleModel newRole = newRoles.get(rawRole.getName());
            newRole.setAssociatedRoles(rawRole.getAssociatedRolesIds().stream()
                    .map(newRoles::get).collect(Collectors.toSet()));
        });
    }

    public Optional<CustomRoleModel> getRoleByName(String id, RealmModel realmModel) {
        return Optional.ofNullable(rolesByNames.get(id)).map(role -> {
            role.setRealmModel(realmModel);
            return role;
        });
    }

}