package me.krzysztofprogramming.userservice.roles;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
class RolePagedResponseDto extends PagedModel<EntityModel<RoleEntity>> {
    private Set<EntityModel<RoleEntity>> associatedRoles;

    private RolePagedResponseDto(PagedModel<EntityModel<RoleEntity>> pagedModel) {
        super(pagedModel.getContent(), pagedModel.getMetadata(), Collections.emptySet(), pagedModel.getResolvableType());
    }

    public static RolePagedResponseDto of(PagedModel<EntityModel<RoleEntity>> pagedModel) {
        Set<RoleEntity> pagedContent = pagedModel.getContent().stream()
                .map(EntityModel::getContent).collect(Collectors.toSet());
        Set<RoleEntity> associatedRoles = pagedContent.stream()
                .flatMap(role -> role.getAssociatedRoles().stream()).collect(Collectors.toSet());

        associatedRoles.removeAll(pagedContent);

        RolePagedResponseDto rolePagedResponseDto = new RolePagedResponseDto(pagedModel);
        rolePagedResponseDto.setAssociatedRoles(associatedRoles.stream().map(EntityModel::of).collect(Collectors.toSet()));
        return rolePagedResponseDto;
    }
}
