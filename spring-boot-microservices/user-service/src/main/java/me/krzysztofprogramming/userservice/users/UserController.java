package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RepositoryRestController
class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("validate")
    @ResponseStatus(HttpStatus.OK)
    public void validateUserCredentials(@RequestBody @Valid UserCredentialsValidationRequestDto requestDto) {
        userService.validateUserCredentials(requestDto.getUsername(), requestDto.getPassword());
    }

    @GetMapping("users/{id}")
    public SingleUserResponseDto getUserById(@PathVariable("id") Long userId) {
        return SingleUserResponseDto.of(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new));
    }

    @GetMapping("users/search/findByEmail")
    public SingleUserResponseDto getUserByEmail(@RequestParam("email") String email) {
        return SingleUserResponseDto.of(userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new));
    }

    @GetMapping("users/search/findByUsername")
    public SingleUserResponseDto getUserByUsername(@RequestParam("username") String username) {
        return SingleUserResponseDto.of(userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleUserNotFound() {
    }

}
