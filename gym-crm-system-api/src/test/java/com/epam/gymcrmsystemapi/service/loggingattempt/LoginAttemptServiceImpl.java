package com.epam.gymcrmsystemapi.service.loggingattempt;

import com.epam.gymcrmsystemapi.model.loginattempt.LoginAttempt;
import com.epam.gymcrmsystemapi.repository.LoginAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginAttemptServiceTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    private final String username = "John.Doe";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginFailed_whenNewUser() {
        when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.empty());

        loginAttemptService.loginFailed(username);

        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }

    @Test
    void testLoginFailed_whenExistingUser() {
        LoginAttempt existingAttempt = new LoginAttempt(username, 1, OffsetDateTime.now().minusMinutes(1));
        when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(existingAttempt));

        loginAttemptService.loginFailed(username);

        assertEquals(2, existingAttempt.getAttempts());
        verify(loginAttemptRepository, times(1)).save(existingAttempt);
    }

    @Test
    void testLoginSucceeded() {
        LoginAttempt existingAttempt = new LoginAttempt(username, 1, OffsetDateTime.now());
        when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(existingAttempt));

        loginAttemptService.loginSucceeded(username);

        verify(loginAttemptRepository, times(1)).delete(existingAttempt);
    }

    @Test
    void testIsBlocked_whenUserNotFound() {
        when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.empty());

        boolean isBlocked = loginAttemptService.isBlocked(username);

        assertFalse(isBlocked);
    }

    @Test
    void testIsBlocked_whenUserBlocked() {
        LoginAttempt blockedAttempt = new LoginAttempt(username, 3, OffsetDateTime.now());
        when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(blockedAttempt));

        boolean isBlocked = loginAttemptService.isBlocked(username);

        assertTrue(isBlocked);
    }

    @Test
    void testIsBlocked_whenUserNotBlocked() {
        LoginAttempt blockedAttempt = new LoginAttempt(username, 3, OffsetDateTime.now().minusMinutes(10));
        when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(blockedAttempt));

        boolean isBlocked = loginAttemptService.isBlocked(username);

        assertFalse(isBlocked);
    }
}
