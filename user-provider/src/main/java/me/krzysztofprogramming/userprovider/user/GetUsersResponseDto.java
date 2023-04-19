package me.krzysztofprogramming.userprovider.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.krzysztofprogramming.userprovider.client.PageResponseDto;
import me.krzysztofprogramming.userprovider.roles.RoleResponseDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class GetUsersResponseDto {
    private GetUsersResponseListDto _embedded;
    private Set<RoleResponseDto> usersAssociatedRoles;
    private PageResponseDto page;
}
