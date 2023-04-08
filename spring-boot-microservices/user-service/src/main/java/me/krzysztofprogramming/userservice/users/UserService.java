package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.users.models.WrongCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void validateUserCredentials(String username, String password) {
        userEntityRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getHashedPassword()))
                .orElseThrow(WrongCredentialsException::new);
    }
}
