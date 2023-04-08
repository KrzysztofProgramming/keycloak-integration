package me.krzysztofprogramming.userservice.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = RoleEntity.TABLE_NAME)
public class RoleEntity {

    public static final String TABLE_NAME = "role_model";
    @Id
    @EqualsAndHashCode.Include
    private String name;

    @ManyToMany
    @JoinTable(
            name = "role_role",
            joinColumns = @JoinColumn(name = "role1_name", referencedColumnName = "name"),
            inverseJoinColumns = @JoinColumn(name = "role2_name", referencedColumnName = "name")
    )
    private Set<RoleEntity> associatedRoles;

}