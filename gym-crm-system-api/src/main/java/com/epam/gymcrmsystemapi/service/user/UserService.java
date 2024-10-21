package com.epam.gymcrmsystemapi.service.user;

import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import com.epam.gymcrmsystemapi.service.user.password.PasswordGenerator;
import com.epam.gymcrmsystemapi.service.user.username.UsernameGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserOperations {

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(@Qualifier("usernameGeneratorImpl") UsernameGenerator usernameGenerator,
                       PasswordGenerator passwordGenerator,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository) {
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public User save(String firstName, String lastName) {
        String username = usernameGenerator.calculateUsername(firstName, lastName);
        String password = passwordGenerator.generateRandomPassword();

        var user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return new User(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                password,
                user.getStatus()
        );
    }
}
