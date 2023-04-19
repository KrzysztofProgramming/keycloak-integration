package me.krzysztofprogramming.userservice.roles;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("roles/")
@AllArgsConstructor
class RoleController {
    private final RoleRepository roleRepository;

    @GetMapping("byId/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SingleRoleResponseDto getRoleById(@PathVariable("id") String roleId) {
        return SingleRoleResponseDto.of(roleRepository.findById(roleId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND)));
    }
}
