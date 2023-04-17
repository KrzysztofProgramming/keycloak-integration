package me.krzysztofprogramming.userservice.users;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userservice.users.models.UserNotFoundException;
import me.krzysztofprogramming.userservice.users.models.WrongCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@Slf4j
class UserController {

    private final UserService userService;

    private final UserRepository userRepository;


    @PostMapping("validate")
    @ResponseStatus(HttpStatus.OK)
    public void validateUserCredentials(@RequestBody @Valid UserCredentialsValidationRequestDto requestDto) {
        userService.validateUserCredentials(requestDto.getUsername(), requestDto.getPassword());
    }

    @GetMapping("users/byId/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SingleUserResponseDto getUserById(@PathVariable("id") Long userId) {
        return SingleUserResponseDto.of(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new));
    }

    @GetMapping("users/byEmail/{email}")
    @ResponseStatus(HttpStatus.OK)
    public SingleUserResponseDto getUserByEmail(@PathVariable("email") String email) {
        return SingleUserResponseDto.of(userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new));
    }

    @GetMapping("users/byUsername/{username}")
    @ResponseStatus(HttpStatus.OK)
    public SingleUserResponseDto getUserByUsername(@PathVariable("username") String username) {
        return SingleUserResponseDto.of(userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleUserNotFound() {
    }

    @ExceptionHandler({IllegalArgumentException.class, WrongCredentialsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleWrongPassword() {
    }

}
