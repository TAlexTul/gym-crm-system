package com.epam.gymcrmsystemapi.service.user.username;

import com.epam.gymcrmsystemapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsernameGeneratorImplTest {

    @InjectMocks
    private UsernameGeneratorImpl usernameGenerator;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateUsername_whenUsernameIsNotExist() {
        String firstName = "John";
        String lastName = "Doe";

        when(userRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(false);

        String username = usernameGenerator.calculateUsername(firstName, lastName);

        assertNotNull(username);
        assertEquals("John.Doe", username);
        verify(userRepository, only()).existsByFirstNameAndLastName(firstName, lastName);
    }

    @Test
    void testCalculateUsername_whenUsernameIsExist() {
        String firstName = "John";
        String lastName = "Doe";

        when(userRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                usernameGenerator.calculateUsername(firstName, lastName)
        );

        assertEquals("User with first name 'John', last name 'Doe' is already taken", exception.getReason());
        verify(userRepository, only()).existsByFirstNameAndLastName(firstName, lastName);
    }
}
