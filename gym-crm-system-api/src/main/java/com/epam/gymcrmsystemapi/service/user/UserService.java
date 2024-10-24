package com.epam.gymcrmsystemapi.service.user;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.exceptions.UserExceptions;
import com.epam.gymcrmsystemapi.exceptions.auth.AuthorityExceptions;
import com.epam.gymcrmsystemapi.model.auth.CustomUserDetails;
import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserAuthority;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.model.user.request.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.request.UserSaveRequest;
import com.epam.gymcrmsystemapi.repository.AuthorityRepository;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import com.epam.gymcrmsystemapi.service.user.password.PasswordGenerator;
import com.epam.gymcrmsystemapi.service.user.username.UsernameGenerator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService, UserOperations {

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserService(UsernameGenerator usernameGenerator,
                       PasswordGenerator passwordGenerator,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       AuthorityRepository authorityRepository) {
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));

        return new CustomUserDetails(user);
    }

    @Override
    public User save(String firstName, String lastName, KnownAuthority authority) {
        String username = usernameGenerator.calculateUsername(firstName, lastName);
        String password = passwordGenerator.generateRandomPassword();

        var user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(UserStatus.ACTIVE);
        user.setAuthorities(getRegularUserAuthorities(authority));
        userRepository.save(user);

        return new User(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                password,
                user.getStatus(),
                user.getAuthorities()
        );
    }

    public void mergeAdmins(List<UserSaveRequest> requests) {
        if (requests.isEmpty()) return;
        Map<KnownAuthority, UserAuthority> authorities = getAdminAuthorities();
        if (requests.isEmpty()) return;
        for (UserSaveRequest request : requests) {
            String firstName = request.firstName();
            String lastName = request.lastName();
            String username = request.username();
            User user = userRepository.findByUsername(username).orElseGet(() -> {
                var newUser = new User();
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setUsername(username);
                return newUser;
            });
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setStatus(UserStatus.ACTIVE);
            user.getAuthorities().putAll(authorities);
            userRepository.save(user);
        }
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

    private Map<KnownAuthority, UserAuthority> getAdminAuthorities() {
        return authorityRepository.findAllByIdIn(AuthorityRepository.ADMIN_AUTHORITIES)
                .collect(Collectors.toMap(
                        UserAuthority::getId,
                        Function.identity(),
                        (e1, e2) -> e2,
                        () -> new EnumMap<>(KnownAuthority.class)));
    }

    private Map<KnownAuthority, UserAuthority> getRegularUserAuthorities(KnownAuthority authority) {
        Map<KnownAuthority, UserAuthority> authorities = new EnumMap<>(KnownAuthority.class);
        UserAuthority auth;
        if (authority.equals(KnownAuthority.ROLE_TRAINEE)) {
            auth = authorityRepository
                    .findById(KnownAuthority.ROLE_TRAINEE)
                    .orElseThrow(() -> AuthorityExceptions.authorityNotFound(KnownAuthority.ROLE_TRAINEE.name()));
            authorities.put(KnownAuthority.ROLE_TRAINEE, auth);
        } else {
            auth = authorityRepository
                    .findById(KnownAuthority.ROLE_TRAINER)
                    .orElseThrow(() -> AuthorityExceptions.authorityNotFound(KnownAuthority.ROLE_TRAINER.name()));
            authorities.put(KnownAuthority.ROLE_TRAINER, auth);
        }
        return authorities;
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
