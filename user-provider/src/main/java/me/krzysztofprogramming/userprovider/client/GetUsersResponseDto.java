package me.krzysztofprogramming.userprovider.client;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.krzysztofprogramming.userprovider.client.model.SingleRoleResponseDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUsersResponseDto {
    private GetUsersResponseListDto _embedded;
    private Set<SingleRoleResponseDto> usersAssociatedRoles;
    private PageResponseDto page;
}
