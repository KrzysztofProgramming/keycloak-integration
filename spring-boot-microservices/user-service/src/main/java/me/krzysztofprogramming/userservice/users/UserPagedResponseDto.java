package me.krzysztofprogramming.userservice.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.krzysztofprogramming.userservice.roles.RoleEntity;
import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
class UserPagedResponseDto extends PagedModel<EntityModel<UserEntity>> {


    private Set<EntityModel<RoleEntity>> usersAssociatedRoles;

    private UserPagedResponseDto(PagedModel<EntityModel<UserEntity>> pagedModel) {
        super(
                pagedModel.getContent()
                        .stream()
                        .map(userModel -> EntityModel.of(Objects.requireNonNull(userModel.getContent())))
                        .collect(Collectors.toList()),
                pagedModel.getMetadata(), Collections.emptyList(), pagedModel.getResolvableType()
        );
    }

    public static UserPagedResponseDto of(PagedModel<EntityModel<UserEntity>> pagedModel) {
        Set<RoleEntity> associatedRoles = new HashSet<>();
        pagedModel.getContent().forEach(entity -> {
            UserEntity userEntity = entity.getContent();
            if (userEntity == null) return;
            addRoles(userEntity.getUserRoles(), associatedRoles);
        });

        UserPagedResponseDto responseDto = new UserPagedResponseDto(pagedModel);
        responseDto.setUsersAssociatedRoles(associatedRoles.stream().map(EntityModel::of).collect(Collectors.toSet()));
        return responseDto;
    }

    static void addRoles(Set<RoleEntity> rolesToAdd, Set<RoleEntity> destination) {
        rolesToAdd.forEach(roleEntity -> {
            if (!destination.add(roleEntity)) return;
            addRoles(roleEntity.getAssociatedRoles(), destination);
        });
    }
}
