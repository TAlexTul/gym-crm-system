package com.epam.gymcrmsystemapi.service.auth;

import com.epam.gymcrmsystemapi.config.security.properties.AdminProperties;
import com.epam.gymcrmsystemapi.config.security.properties.JWTProperties;
import com.epam.gymcrmsystemapi.config.security.properties.SecurityProperties;
import com.epam.gymcrmsystemapi.model.auth.CustomUserDetails;
import com.epam.gymcrmsystemapi.model.auth.RefreshToken;
import com.epam.gymcrmsystemapi.model.auth.response.AccessTokenResponse;
import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserAuthority;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.RefreshTokenRepository;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class JWTAuthServiceTest {

    private JWTAuthService jwtAuthService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;

    private final static String SECRET = "eitu9aichae7eitee9XiciweishohW3pieshaifasosai5xie9Oomobulohyu8ie";
    private final Duration jwtExpiration = Duration.ofMinutes(15);
    private final Duration refreshExpiration = Duration.ofDays(7);

    @BeforeEach
    void setUp() {
        JWTProperties jwtProperties = new JWTProperties();
        jwtProperties.setSecret(SECRET);
        jwtProperties.setAccessExpireIn(jwtExpiration);
        jwtProperties.setRefreshExpireIn(refreshExpiration);

        AdminProperties adminProperties = new AdminProperties();
        adminProperties.setFirstName("John");
        adminProperties.setLastName("Doe");
        adminProperties.setPassword("aB9dE4fGhJ".toCharArray());

        Map<String, AdminProperties> admins = new HashMap<>();
        admins.put("John.Doe", adminProperties);

        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setJwt(jwtProperties);
        securityProperties.setAdmins(admins);

        MockitoAnnotations.openMocks(this);

        jwtAuthService = new JWTAuthService(securityProperties, refreshTokenRepository, userRepository);
    }

    @Test
    void testGetToken() {
        User user = getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        RefreshToken refreshToken = getRefreshToken(user);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        AccessTokenResponse tokenResponse = jwtAuthService.getToken(userDetails);

        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.accessToken());
        assertNotNull(tokenResponse.refreshToken());
        assertEquals(jwtExpiration.toSeconds(), tokenResponse.expireIn());

        verify(refreshTokenRepository, only()).save(any(RefreshToken.class));
    }

    private RefreshToken getRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setValue(UUID.randomUUID());
        refreshToken.setIssuedAt(OffsetDateTime.now());
        refreshToken.setExpireAt(OffsetDateTime.now().plus(refreshExpiration));
        refreshToken.setUser(user);
        return refreshToken;
    }

    private User getUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Jonh");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        user.getAuthorities().put(KnownAuthority.ROLE_TRAINEE, new UserAuthority());
        return user;
    }
}
