package com.epam.gymcrmsystemapi.config.logingattempt;

import com.epam.gymcrmsystemapi.config.loggingattempt.CustomAuthenticationProvider;
import com.epam.gymcrmsystemapi.service.loggingattempt.LoggingAttemptOperations;
import com.epam.gymcrmsystemapi.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomAuthenticationProviderTest {

    private CustomAuthenticationProvider customAuthenticationProvider;
    private UserService userService;
    private LoggingAttemptOperations loggingAttemptOperations;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        loggingAttemptOperations = mock(LoggingAttemptOperations.class);
        passwordEncoder = mock(PasswordEncoder.class);
        customAuthenticationProvider = new CustomAuthenticationProvider(userService, loggingAttemptOperations, passwordEncoder);
    }

    @Test
    void testAuthenticate_Success() {
        String username = "John.Doe";
        String password = "password";
        String encodedPassword = "encodedPassword";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getPassword()).thenReturn(encodedPassword);
        when(userDetails.getAuthorities()).thenReturn(null);

        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

        Authentication result = customAuthenticationProvider.authenticate(authentication);

        assertNotNull(result);
        assertEquals(username, result.getName());
        verify(loggingAttemptOperations).loginSucceeded(username);
    }

    @Test
    void testAuthenticate_Failure() {
        String username = "John.Doe";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getPassword()).thenReturn(encodedPassword);
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> customAuthenticationProvider.authenticate(authentication));

        assertEquals("User with user name 'John.Doe' entered an incorrect password", exception.getMessage());
        verify(loggingAttemptOperations).loginFailed(username);
    }

    @Test
    void testSupports() {
        assertTrue(customAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(customAuthenticationProvider.supports(Object.class));
    }
}
