package me.krzysztofprogramming.userservice.roles;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = RoleEntity.TABLE_NAME)
public class RoleEntity {
    public final static String TABLE_NAME = "role_table";
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private String id;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "ROLES_ASSOCIATIONS_TABLE",
            joinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id")
    )
    @ToString.Exclude
    @RestResource(exported = false)
    @JsonSetter
    private Set<RoleEntity> parentRoles;

    @JsonGetter("parentRoles")
    public List<String> serializeParentRoles() {
        return parentRoles.stream().map(RoleEntity::getId).collect(Collectors.toList());
    }
}