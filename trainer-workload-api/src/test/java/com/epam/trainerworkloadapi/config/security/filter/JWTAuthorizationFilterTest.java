package com.epam.trainerworkloadapi.config.security.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.epam.trainerworkloadapi.config.security.SecurityConstants;
import com.epam.trainerworkloadapi.model.user.KnownAuthority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JWTAuthorizationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private Algorithm algorithm;

    private JWTAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String secret = "secret";

        algorithm = Algorithm.HMAC512(secret.getBytes());
        jwtAuthorizationFilter = new JWTAuthorizationFilter(authenticationManager, secret);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidToken() throws Exception {
        String validToken = "Bearer " + createValidJWT();
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validToken);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(securityContext);

        jwtAuthorizationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testNoAuthorizationHeader() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtAuthorizationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInvalidToken() throws Exception {
        String invalidToken = "Bearer invalid.jwt.token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidToken);

        jwtAuthorizationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    private String createValidJWT() {
        return com.auth0.jwt.JWT.create()
                .withSubject("John.Doe")
                .withClaim(SecurityConstants.AUTHORITIES_CLAIM,
                        Collections.singletonList(KnownAuthority.ROLE_ADMIN.name()))
                .sign(algorithm);
    }
}
