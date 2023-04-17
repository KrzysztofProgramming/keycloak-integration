package me.krzysztofprogramming.userservice.users;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import me.krzysztofprogramming.userservice.roles.RoleEntity;
import me.krzysztofprogramming.userservice.users.models.UserEntity;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SingleUserResponseDto {
    @JsonUnwrapped
    private UserEntity userEntity;
    private Set<RoleEntity> associatedRoles;

    private SingleUserResponseDto(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public static SingleUserResponseDto of(UserEntity userEntity) {
        SingleUserResponseDto responseDto = new SingleUserResponseDto(userEntity);
        Set<RoleEntity> associatedRoles = new HashSet<>();
        UserPagedResponseDto.addRoles(userEntity.getUserRoles(), associatedRoles);
        responseDto.setAssociatedRoles(associatedRoles);
        return responseDto;
    }
}
