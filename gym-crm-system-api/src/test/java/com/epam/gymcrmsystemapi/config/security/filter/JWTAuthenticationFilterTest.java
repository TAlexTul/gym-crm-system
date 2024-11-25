package com.epam.gymcrmsystemapi.config.security.filter;

import com.epam.gymcrmsystemapi.config.security.filters.JWTAuthenticationFilter;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JWTAuthenticationFilterTest {

    private JWTAuthenticationFilter jwtAuthenticationFilter;
    private AuthenticationManager authenticationManager;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        objectMapper = new ObjectMapper();
        jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager, objectMapper);
    }

    @Test
    void testAttemptAuthentication_Success() throws IOException {
        String login = "John.Doe";
        String password = "testPassword";
        SignInRequest signInRequest = new SignInRequest(login, password);

        String requestBody = objectMapper.writeValueAsString(signInRequest);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return inputStream.read();
            }
        });

        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        Authentication result = jwtAuthenticationFilter.attemptAuthentication(request, response);

        assertEquals(authentication, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
