package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
class UserEventHandler {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @HandleBeforeSave
    @HandleBeforeCreate
    public void interceptUserBeforeSaving(UserEntity userEntity) {
        hashPassword(userEntity);
    }

    private void hashPassword(UserEntity userEntity) {
        if (userEntity.getHashedPassword() != null)
            userEntity.setHashedPassword(passwordEncoder.encode(userEntity.getHashedPassword()));
    }
}
