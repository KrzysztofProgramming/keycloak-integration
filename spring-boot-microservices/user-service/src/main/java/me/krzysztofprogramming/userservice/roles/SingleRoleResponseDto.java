package me.krzysztofprogramming.userservice.roles;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SingleRoleResponseDto {
    @JsonUnwrapped
    private RoleEntity roleEntity;

    private Set<RoleEntity> associatedRoles;

    private SingleRoleResponseDto(RoleEntity userEntity) {
        this.roleEntity = userEntity;
    }

    public static SingleRoleResponseDto of(RoleEntity roleEntity) {
        SingleRoleResponseDto responseDto = new SingleRoleResponseDto(roleEntity);
        responseDto.setAssociatedRoles(roleEntity.unwrapAssociatedRoles());
        return responseDto;
    }


}
