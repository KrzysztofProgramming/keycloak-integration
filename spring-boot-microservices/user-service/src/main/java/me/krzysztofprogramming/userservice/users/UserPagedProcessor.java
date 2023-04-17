package me.krzysztofprogramming.userservice.users;

import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class UserPagedProcessor implements RepresentationModelProcessor<PagedModel<EntityModel<UserEntity>>> {

    @Override
    public PagedModel<EntityModel<UserEntity>> process(PagedModel<EntityModel<UserEntity>> model) {
        return UserPagedResponseDto.of(model);
    }
}
