package com.epam.gymcrmsystemapi.service.user;

import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import com.epam.gymcrmsystemapi.service.user.password.PasswordGenerator;
import com.epam.gymcrmsystemapi.service.user.username.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    private final static String OLD_PASSWORD = "aB9dE4fGhJ";
    private final static String NEW_PASSWORD = "cM5dU4fEhL";
    private final static int PASSWORD_STRENGTH = 10;
    private final PasswordEncoder controlEncoder = new BCryptPasswordEncoder(PASSWORD_STRENGTH, new SecureRandom());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_whenSuccess() {
        String firstName = "John";
        String lastName = "Doe";
        String username = String.join(".", firstName, lastName);
        String password = "aB9dE4fGhJ";
        User user = getUser();

        when(usernameGenerator.calculateUsername(firstName, lastName)).thenReturn(username);
        when(passwordGenerator.generateRandomPassword()).thenReturn("aB9dE4fGhJ");
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User response = userService.save(firstName, lastName);

        assertNotNull(response);
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getPassword(), response.getPassword());
        assertEquals(user.getStatus(), response.getStatus());

        verify(usernameGenerator, only()).calculateUsername(firstName, lastName);
        verify(passwordGenerator, only()).generateRandomPassword();
        verify(passwordEncoder, only()).encode(password);
        verify(userRepository, only()).save(any(User.class));
    }

    @Test
    void testChangeStatusById() {
        Long id = 1L;
        User user = getUser();
        UserStatus status = UserStatus.ACTIVE;

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.changeStatusById(id, status);

        verify(userRepository, only()).findById(id);
    }

    @Test
    void testChangeStatusByUsername() {
        String username = "John.Doe";
        User user = getUser();
        UserStatus status = UserStatus.ACTIVE;

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userService.changeStatusByUsername(username, status);

        verify(userRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeLoginDataById() {
        Long id = 1L;
        OverrideLoginRequest request = new OverrideLoginRequest("John.Doe", OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        User user = getUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(OLD_PASSWORD, OLD_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(encodePassword);

        userService.changeLoginDataById(id, request);

        verify(userRepository, times(1)).findById(id);
        assertTrue(controlEncoder.matches(NEW_PASSWORD, encodePassword));
        verify(passwordEncoder, times(1)).matches(OLD_PASSWORD, OLD_PASSWORD);
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
    }

    @Test
    void testChangeLoginDataById_whenPasswordIsNotMatch() {
        Long id = 1L;
        OverrideLoginRequest request = new OverrideLoginRequest("John.Doe", OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        User user = getUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(OLD_PASSWORD, OLD_PASSWORD)).thenReturn(false);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> userService.changeLoginDataById(id, request));

        assertEquals("400 BAD_REQUEST \"Password is incorrect\"", exception.getMessage());

        verify(userRepository, only()).findById(id);
        assertTrue(controlEncoder.matches(NEW_PASSWORD, encodePassword));
    }

    @Test
    void testChangeLoginDataById_whenUserIsNotFound() {
        Long id = 1L;
        OverrideLoginRequest request = new OverrideLoginRequest("John.Doe", OLD_PASSWORD, NEW_PASSWORD);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> userService.changeLoginDataById(id, request));

        assertEquals("404 NOT_FOUND \"User with id '" + id + "' not found\"", exception.getMessage());

        verify(userRepository, only()).findById(id);
    }

    @Test
    void testChangeLoginDataByUsername() {
        String username = "John.Doe";
        OverrideLoginRequest request = new OverrideLoginRequest(username, OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        User user = getUser();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(OLD_PASSWORD, OLD_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(encodePassword);

        userService.changeLoginDataByUsername(username, request);

        verify(userRepository, times(1)).findByUsername(username);
        assertTrue(controlEncoder.matches(NEW_PASSWORD, encodePassword));
        verify(passwordEncoder, times(1)).matches(OLD_PASSWORD, OLD_PASSWORD);
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
    }

    @Test
    void testChangeLoginDataByUsername_whenPasswordIsNotMatch() {
        String username = "John.Doe";
        OverrideLoginRequest request = new OverrideLoginRequest(username, OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        User user = getUser();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(OLD_PASSWORD, OLD_PASSWORD)).thenReturn(false);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> userService.changeLoginDataByUsername(username, request));

        assertEquals("400 BAD_REQUEST \"Password is incorrect\"", exception.getMessage());

        verify(userRepository, only()).findByUsername(username);
        assertTrue(controlEncoder.matches(NEW_PASSWORD, encodePassword));
    }

    @Test
    void testChangeLoginDataByUsername_whenUserIsNotFound() {
        String username = "John.Doe";
        OverrideLoginRequest request = new OverrideLoginRequest(username, OLD_PASSWORD, NEW_PASSWORD);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> userService.changeLoginDataByUsername(username, request));

        assertEquals(
                "404 NOT_FOUND \"User with user name '" + username + "' not found\"",
                exception.getMessage());

        verify(userRepository, only()).findByUsername(username);
    }

    private User getUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword(OLD_PASSWORD);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
