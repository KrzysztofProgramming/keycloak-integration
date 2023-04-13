package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@SuppressWarnings("unused")
@RepositoryRestResource(collectionResourceRel = UserEntity.TABLE_NAME, path = "users")
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

}