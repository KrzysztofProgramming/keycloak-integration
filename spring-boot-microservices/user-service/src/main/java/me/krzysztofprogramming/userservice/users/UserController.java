package me.krzysztofprogramming.userservice.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping()
class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("validate")
    @ResponseStatus(HttpStatus.OK)
    public void validateUserCredentials(@RequestBody @Valid UserCredentialsValidationRequestDto requestDto) {
        userService.validateUserCredentials(requestDto.getUsername(), requestDto.getPassword());
    }
}
