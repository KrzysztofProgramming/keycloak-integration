package me.krzysztofprogramming.userservice.repository;

import me.krzysztofprogramming.userservice.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleModelRepository extends JpaRepository<RoleEntity, String> {
}
