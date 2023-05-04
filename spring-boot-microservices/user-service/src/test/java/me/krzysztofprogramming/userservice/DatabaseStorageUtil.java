package me.krzysztofprogramming.userservice;

import me.krzysztofprogramming.userservice.roles.RoleEntity;
import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatabaseStorageUtil {

    public static final Map<Long, UserEntity> usersMap = new HashMap<>();
    public static final Map<String, RoleEntity> rolesMap = new HashMap<>();
    private final static PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
    private final static UserEntity USER1 = UserEntity.builder()
            .id(1L)
            .username("user1")
            .hashedPassword(passwordEncoder.encode("user1"))
            .firstname("John")
            .lastname("Wonderful")
            .build();

    private final static UserEntity USER2 = UserEntity.builder()
            .id(2L)
            .username("user2")
            .hashedPassword(passwordEncoder.encode("user2"))
            .firstname("Clara")
            .lastname("Spencer")
            .build();

    private final static UserEntity USER3 = UserEntity.builder()
            .id(3L)
            .username("user3")
            .hashedPassword(passwordEncoder.encode("user3"))
            .firstname("John")
            .lastname("Amazing")
            .build();


    private final static RoleEntity ROLE1 = RoleEntity.builder()
            .id("admin")
            .build();

    private final static RoleEntity ROLE2 = RoleEntity.builder()
            .id("A")
            .build();

    private final static RoleEntity ROLE3 = RoleEntity.builder()
            .id("B")
            .build();

    private final static RoleEntity ROLE4 = RoleEntity.builder()
            .id("C")
            .build();

    static {
        usersMap.put(USER1.getId(), USER1);
        usersMap.put(USER2.getId(), USER2);
        usersMap.put(USER3.getId(), USER3);

        rolesMap.put(ROLE1.getId(), ROLE1);
        rolesMap.put(ROLE2.getId(), ROLE2);
        rolesMap.put(ROLE3.getId(), ROLE3);
        rolesMap.put(ROLE4.getId(), ROLE4);

        linkRolesAndUsers();
    }

    private static void unlinkRolesAndUsers() {
        usersMap.values().forEach(user -> user.setUserRoles(Set.of()));
        rolesMap.values().forEach(role -> role.setAssociatedRoles(Set.of()));
    }

    private static void linkRolesAndUsers() {
        ROLE1.setAssociatedRoles(Set.of(ROLE2, ROLE3, ROLE4));
        ROLE2.setAssociatedRoles(Set.of(ROLE3));
        ROLE3.setAssociatedRoles(Set.of(ROLE4));
        ROLE4.setAssociatedRoles(Set.of(ROLE3));

        USER1.setUserRoles(Set.of(ROLE1));
        USER2.setUserRoles(Set.of(ROLE2, ROLE3));
        USER3.setUserRoles(Set.of(ROLE4));
    }

    public static void initDatabase(EntityManager entityManager) {
        unlinkRolesAndUsers();
        usersMap.values().forEach(entityManager::merge);
        rolesMap.values().forEach(entityManager::merge);
        linkRolesAndUsers();
        usersMap.values().forEach(entityManager::merge);
        rolesMap.values().forEach(entityManager::merge);
    }
}
