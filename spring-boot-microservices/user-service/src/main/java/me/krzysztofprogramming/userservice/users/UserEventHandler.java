package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
class UserEventHandler {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @HandleBeforeCreate
    public void interceptUserBeforeCreating(UserEntity userEntity) {
        hashPassword(userEntity);
    }

    private void hashPassword(UserEntity userEntity) {
        userEntity.setHashedPassword(passwordEncoder.encode(userEntity.getHashedPassword()));
    }
}
