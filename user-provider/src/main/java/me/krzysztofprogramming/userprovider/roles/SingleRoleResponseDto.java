package me.krzysztofprogramming.userprovider.roles;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.Set;

@Data
class SingleRoleResponseDto {
    @JsonUnwrapped
    private RoleResponseDto roleResponseDto;

    private Set<RoleResponseDto> associatedRoles;
}
