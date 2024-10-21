package com.epam.gymcrmsystemapi.service.user;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private User getUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
