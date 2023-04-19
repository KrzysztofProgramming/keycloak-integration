package me.krzysztofprogramming.userprovider.roles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.krzysztofprogramming.userprovider.client.PageResponseDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public
class GetRolesResponseDto {
    private GetRolesResponseListDto _embedded;
    private Set<RoleResponseDto> associatedRoles;
    private PageResponseDto page;
}
