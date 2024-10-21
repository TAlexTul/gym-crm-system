package com.epam.gymcrmsystemapi.service.user.username;

import com.epam.gymcrmsystemapi.repository.UserRepository;
import com.epam.gymcrmsystemapi.service.user.username.UsernameGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        when(userRepository.selectMaxId()).thenReturn(1L);

        String username = usernameGenerator.calculateUsername(firstName, lastName);

        assertNotNull(username);
        assertEquals("John.Doe.2", username);
        verify(userRepository, times(1)).existsByFirstNameAndLastName(firstName, lastName);
        verify(userRepository, times(1)).selectMaxId();
        verifyNoMoreInteractions(userRepository);
    }
}
