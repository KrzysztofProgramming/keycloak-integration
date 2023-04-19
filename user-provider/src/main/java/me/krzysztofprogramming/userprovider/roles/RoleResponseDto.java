package me.krzysztofprogramming.userprovider.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoleResponseDto {

    @JsonProperty("id")
    @EqualsAndHashCode.Include
    private String name;
    private String description;
    private Set<String> associatedRolesIds;
}
