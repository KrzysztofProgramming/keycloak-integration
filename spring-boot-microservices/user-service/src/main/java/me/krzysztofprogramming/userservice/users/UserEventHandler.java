package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Component
@RepositoryEventHandler
class UserEventHandler {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @HandleBeforeCreate
    public void interceptUserBeforeCreating(UserEntity userEntity) {
        hashPassword(userEntity);
    }

    @HandleBeforeSave
    @Transactional
    public void interceptBeforeSaving(UserEntity userEntity) {
        entityManager.detach(userEntity);
        UserEntity existingUser = entityManager.find(UserEntity.class, userEntity.getId());

        if (!userEntity.getHashedPassword().equals(existingUser.getHashedPassword())) {
            hashPassword(userEntity);
        }
        entityManager.merge(userEntity);
    }

    private void hashPassword(UserEntity userEntity) {
        userEntity.setHashedPassword(passwordEncoder.encode(userEntity.getHashedPassword()));
    }
}
