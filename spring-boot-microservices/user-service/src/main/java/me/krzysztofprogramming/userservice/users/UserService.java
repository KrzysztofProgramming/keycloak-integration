package me.krzysztofprogramming.userservice.users;

import lombok.AllArgsConstructor;
import me.krzysztofprogramming.userservice.users.models.WrongCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void validateUserCredentials(String username, String password) {
        userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getHashedPassword()))
                .orElseThrow(WrongCredentialsException::new);
    }
}
