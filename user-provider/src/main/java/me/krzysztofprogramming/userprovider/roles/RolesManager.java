package me.krzysztofprogramming.userprovider.roles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.krzysztofprogramming.userprovider.client.model.SingleRoleResponseDto;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
public class RolesManager {

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, CustomRoleModel> rolesByIds = new HashMap<>();

    public static RolesManager of(Set<SingleRoleResponseDto> rawRoles) {
        RolesManager rolesManager = new RolesManager();
        rolesManager.addRoles(rawRoles);
        return rolesManager;
    }

    private static CustomRoleModel mapToCustomRole(SingleRoleResponseDto roleResponseDto) {
        return new CustomRoleModel(
                roleResponseDto.getId(),
                roleResponseDto.getDescription(),
                Collections.emptySet()
        );
    }

    public void addRoles(Set<SingleRoleResponseDto> rawRoles) {
        Map<String, CustomRoleModel> newRoles = rawRoles.stream().map(RolesManager::mapToCustomRole)
                .collect(Collectors.toMap(CustomRoleModel::getId, Function.identity()));
        linkRoles(newRoles, rawRoles);
        mergeWithSavedRoles(newRoles);
    }

    public CustomRoleModel getRoleById(String id) {
        return rolesByIds.get(id);
    }

    private void mergeWithSavedRoles(Map<String, CustomRoleModel> newRoles) {
        Iterator<CustomRoleModel> iterator = newRoles.values().iterator();
        while (iterator.hasNext()) {
            CustomRoleModel newRole = iterator.next();
            CustomRoleModel oldRole = rolesByIds.get(newRole.getId());
            if (oldRole == null) continue;

            oldRole.setDescription(newRole.getDescription());
            oldRole.setAssociatedRoles(newRole.getAssociatedRoles());
            iterator.remove();
        }

        rolesByIds.putAll(newRoles);
    }

    private void linkRoles(Map<String, CustomRoleModel> newRoles, Set<SingleRoleResponseDto> rawRoles) {
        rawRoles.forEach(rawRole -> {
            CustomRoleModel newRole = newRoles.get(rawRole.getId());
            newRole.setAssociatedRoles(rawRole.getAssociatedRolesIds().stream()
                    .map(newRoles::get).collect(Collectors.toSet()));
        });
    }
}
