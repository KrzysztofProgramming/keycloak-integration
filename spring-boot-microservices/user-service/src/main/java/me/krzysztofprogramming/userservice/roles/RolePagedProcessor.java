package me.krzysztofprogramming.userservice.roles;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
class RolePagedProcessor implements RepresentationModelProcessor<PagedModel<EntityModel<RoleEntity>>> {
    @Override
    public PagedModel<EntityModel<RoleEntity>> process(PagedModel<EntityModel<RoleEntity>> model) {
        return RolePagedResponseDto.of(model);
    }
}
