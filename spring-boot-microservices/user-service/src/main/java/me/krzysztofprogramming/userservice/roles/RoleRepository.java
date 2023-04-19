package me.krzysztofprogramming.userservice.roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
@RepositoryRestResource(collectionResourceRel = RoleEntity.TABLE_NAME, path = "roles")
public interface RoleRepository extends JpaRepository<RoleEntity, String> {

    @RestResource(path = "byIds")
    List<RoleEntity> findAllByIdIn(Set<String> ids);
}