package me.krzysztofprogramming.userservice.roles;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = RoleEntity.TABLE_NAME)
@Slf4j
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
    @JsonProperty("associatedRolesIds")
    private Set<RoleEntity> associatedRoles = new HashSet<>();

    public RoleEntity(String id) {
        this.id = id;
    }

    @JsonGetter("associatedRolesIds")
    public Set<String> serializeAssociatedRoles() {
        log.debug("Serializing roles {}", associatedRoles);
        return associatedRoles == null ? Collections.emptySet() : associatedRoles.stream().map(RoleEntity::getId).collect(Collectors.toSet());
    }

    @JsonSetter("associatedRolesIds")
    public void deserializeAssociatedRoles(Set<String> associatedRolesIds) {
        log.debug("Deserializing roles {}", associatedRolesIds);
        if (associatedRolesIds == null) {
            setAssociatedRoles(Collections.emptySet());
            return;
        }
        setAssociatedRoles(associatedRolesIds.stream().map(RoleEntity::new).collect(Collectors.toSet()));
    }
}