package me.krzysztofprogramming.userprovider.client.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.krzysztofprogramming.userprovider.user.CustomUserModel;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleUserResponseDto {
    @JsonUnwrapped
    private CustomUserModel customUserModel;

    private Set<SingleRoleResponseDto> associatedRoles;
}
