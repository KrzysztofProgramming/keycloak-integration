package me.krzysztofprogramming.userprovider.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleRoleResponseDto {

    @JsonProperty("id")
    private String name;
    private String description;
    private Set<String> associatedRolesIds;
}
