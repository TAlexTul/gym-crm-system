package com.epam.gymcrmsystemapi.service.user;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.exceptions.UserExceptions;
import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import com.epam.gymcrmsystemapi.service.user.password.PasswordGenerator;
import com.epam.gymcrmsystemapi.service.user.username.UsernameGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements UserOperations {

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UsernameGenerator usernameGenerator,
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

    @Override
    public void changeStatusById(Long id, UserStatus status) {
        User user = getUser(id);
        if (user.getStatus() != status) {
            user.setStatus(status);
        }
    }

    @Override
    public void changeStatusByUsername(String username, UserStatus status) {
        User user = getUser(username);
        if (user.getStatus() != status) {
            user.setStatus(status);
        }
    }

    @Override
    public void changeLoginDataById(long id, OverrideLoginRequest request) {
        User user = getUser(id);
        changePassword(user, request.oldPassword(), request.newPassword());
    }

    @Override
    public void changeLoginDataByUsername(String username, OverrideLoginRequest request) {
        User user = getUser(username);
        changePassword(user, request.oldPassword(), request.newPassword());
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> UserExceptions.userNotFound(id));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> UserExceptions.userNotFound(username));
    }

    private void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw TraineeExceptions.wrongPassword();
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
