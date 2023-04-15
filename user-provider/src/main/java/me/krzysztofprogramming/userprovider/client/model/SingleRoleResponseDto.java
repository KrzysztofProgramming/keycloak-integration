package me.krzysztofprogramming.userprovider.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleRoleResponseDto {
    private String id;
    private String description;
    private Set<String> associatedRolesIds;
}
