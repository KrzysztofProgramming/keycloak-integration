package me.krzysztofprogramming.userservice.entity;

import me.krzysztofprogramming.userservice.users.UserEntityRepository;
import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.Optional;

@DataJpaTest
public class UserEntityTest {

    private final Long USER_ID = 1L;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    @Disabled
    public void shouldSaveUser() {
        //given
        UserEntity user = createUser();

        //when
        entityManager.persist(user);
        entityManager.flush();

        //then
        Optional<UserEntity> resultUser = userEntityRepository.findById(USER_ID);
        Assertions.assertThat(resultUser)
                .isPresent().get()
                .returns(user.getLastname(), UserEntity::getLastname)
                .returns(user.getEmail(), UserEntity::getEmail)
                .returns(user.getId(), UserEntity::getId)
                .returns(user.getFirstname(), UserEntity::getFirstname)
                .returns(user.getHashedPassword(), UserEntity::getHashedPassword)
                .returns(user.getCreatedDate(), UserEntity::getCreatedDate);
//                .returns(1, userEntity -> userEntity.getUserRoles().size());
//        Assertions.assertThat(resultUser.get().getUserRoles().stream().findFirst())
//                .isPresent().get().isEqualTo(createRole()).usingComparator(this::compareRolesDeeply);

    }

    @Test
    @Disabled
    public void shouldViolateUniqueConstraints() {
        //given
        UserEntity user = createUser();
        entityManager.persist(user);
        entityManager.flush();
        UserEntity user2 = createUser();
        user2.setId(2L);

        //when
        entityManager.persist(user2);
        Assertions.assertThatThrownBy(entityManager::flush)
                .getCause().isInstanceOf(ConstraintViolationException.class);
    }

    private int compareRolesDeeply(RoleEntity e1, RoleEntity e2) {
        return e1.getAssociatedRoles().equals(e2.getAssociatedRoles()) ? 0 : 1;
    }

    private UserEntity createUser() {
        return UserEntity.builder()
                .id(USER_ID)
                .createdDate(new Date(123L))
                .username("username")
                .firstname("firstname")
                .lastname("lastname")
                .email("email")
                .hashedPassword("password")
//                .userRoles(Set.of(createRole()))
                .build();
    }

//    private RoleEntity createRole() {
//        return RoleEntity.builder().name("admin").build();
//    }
}
