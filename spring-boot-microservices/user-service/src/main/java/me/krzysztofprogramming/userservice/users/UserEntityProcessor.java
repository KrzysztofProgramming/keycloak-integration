package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class UserEntityProcessor implements RepresentationModelProcessor<EntityModel<UserEntity>> {
    @Override
    public EntityModel<UserEntity> process(EntityModel<UserEntity> model) {
        return EntityModel.of(Objects.requireNonNull(model.getContent()));
    }
}