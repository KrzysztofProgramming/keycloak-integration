package me.krzysztofprogramming.userservice.entity;

import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;

@DataJpaTest
@EnableJpaAuditing
public class UserEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void shouldSaveUser() {
        //given
        UserEntity user = createUser();

        //when
        entityManager.persist(user);
        entityManager.flush();

        //then
        Optional<UserEntity> resultUser = Optional.ofNullable(entityManager.find(UserEntity.class, user.getId()));
        Assertions.assertThat(resultUser)
                .isPresent().get()
                .returns(user.getLastname(), UserEntity::getLastname)
                .returns(user.getEmail(), UserEntity::getEmail)
                .returns(user.getId(), UserEntity::getId)
                .returns(user.getFirstname(), UserEntity::getFirstname)
                .returns(user.getHashedPassword(), UserEntity::getHashedPassword)
                .doesNotReturn(null, UserEntity::getCreatedDate)
                .doesNotReturn(null, UserEntity::getLastModifiedDate);
    }

    @Test
    @Transactional
    public void shouldViolateUniqueConstraints() {
        //given
        UserEntity user = createUser();
        entityManager.persist(user);
        entityManager.flush();
        UserEntity user2 = createUser();

        //when
        entityManager.persist(user2);
        Assertions.assertThatThrownBy(entityManager::flush)
                .getCause().isInstanceOf(ConstraintViolationException.class);
    }

    private UserEntity createUser() {
        return UserEntity.builder()
                .username("username")
                .firstname("firstname")
                .lastname("lastname")
                .email("email")
                .hashedPassword("password")
                .build();
    }
}
