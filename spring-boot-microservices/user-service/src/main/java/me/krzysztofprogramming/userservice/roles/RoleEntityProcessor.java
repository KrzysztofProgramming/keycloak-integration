package me.krzysztofprogramming.userservice.roles;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class RoleEntityProcessor implements RepresentationModelProcessor<EntityModel<RoleEntity>> {
    @Override
    public EntityModel<RoleEntity> process(EntityModel<RoleEntity> model) {
        return EntityModel.of(Objects.requireNonNull(model.getContent()));
    }
}
