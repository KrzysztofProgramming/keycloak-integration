package me.krzysztofprogramming.userprovider.user;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.krzysztofprogramming.userprovider.roles.RoleResponseDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleUserResponseDto {
    @JsonUnwrapped
    private CustomUserModel customUserModel;

    private Set<RoleResponseDto> associatedRoles;
}
