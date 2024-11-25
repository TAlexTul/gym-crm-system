package com.epam.gymcrmsystemapi.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.epam.gymcrmsystemapi.config.security.SecurityConstants;
import com.epam.gymcrmsystemapi.config.security.properties.SecurityProperties;
import com.epam.gymcrmsystemapi.exceptions.auth.InvalidRefreshTokenException;
import com.epam.gymcrmsystemapi.model.auth.CustomUserDetails;
import com.epam.gymcrmsystemapi.model.auth.RefreshToken;
import com.epam.gymcrmsystemapi.model.auth.response.AccessTokenResponse;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.RefreshTokenRepository;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@Transactional
public class JWTAuthService implements AuthOperations {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthService.class);

    private final Duration jwtExpiration;
    private final Duration refreshExpiration;
    private final Algorithm algorithm;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JWTAuthService(SecurityProperties securityProperties,
                          RefreshTokenRepository refreshTokenRepository,
                          UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        var jwtProperties = securityProperties.getJwt();
        this.jwtExpiration = jwtProperties.getAccessExpireIn();
        this.refreshExpiration = jwtProperties.getRefreshExpireIn();
        this.algorithm = Algorithm.HMAC512(jwtProperties.getSecret().getBytes());
    }

    @Override
    public AccessTokenResponse getToken(CustomUserDetails userDetails) {
        RefreshToken newToken = issueRefreshToken(userDetails.getSource());
        return response(userDetails.getUsername(), userDetails.getAuthorities(), newToken);
    }

    @Override
    public AccessTokenResponse refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        RefreshToken storedToken = refreshTokenRepository.findIfValid(
                verifyRefreshToken(refreshToken),
                OffsetDateTime.now(),
                UserStatus.ACTIVE
        ).orElseThrow(InvalidRefreshTokenException::new);

        checkIfRotated(storedToken);

        User user = storedToken.getUser();

        var nextToken = issueRefreshToken(user);

        refreshTokenRepository.updateChain(storedToken, nextToken);

        return response(user.getUsername(), user.getAuthorities().keySet(), nextToken);
    }

    @Override
    public void invalidateToken(String refreshToken, String ownerUsername) throws InvalidRefreshTokenException {
        RefreshToken storedToken = refreshTokenRepository.findById(verifyRefreshToken(refreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);
        checkOwner(storedToken, ownerUsername);
        checkIfRotated(storedToken);
        refreshTokenRepository.deleteChain(storedToken);
    }

    private AccessTokenResponse response(String subject,
                                         Collection<? extends GrantedAuthority> authorities,
                                         RefreshToken refreshToken) {
        String accessToken = issueJWT(subject, authorities);
        return new AccessTokenResponse(
                accessToken,
                signRefreshToken(refreshToken),
                jwtExpiration.toSeconds()
        );
    }

    private String issueJWT(String subject, Collection<? extends GrantedAuthority> authorities) {
        long issuedAt = System.currentTimeMillis();
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(issuedAt))
                .withExpiresAt(new Date(issuedAt + jwtExpiration.toMillis()))
                .withArrayClaim(SecurityConstants.AUTHORITIES_CLAIM, authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .sign(algorithm);
    }

    private RefreshToken issueRefreshToken(User user) {
        var refreshToken = new RefreshToken();
        var now = OffsetDateTime.now();
        refreshToken.setIssuedAt(now);
        refreshToken.setExpireAt(now.plus(refreshExpiration));
        refreshToken.setUser(user);
        return refreshTokenRepository.save(refreshToken);
    }

    private String signRefreshToken(RefreshToken token) {
        return JWT.create()
                .withSubject(token.getUser().getUsername())
                .withJWTId(token.getValue().toString())
                .withIssuedAt(Date.from(token.getIssuedAt().toInstant()))
                .withExpiresAt(Date.from(token.getExpireAt().toInstant()))
                .sign(algorithm);
    }

    private UUID verifyRefreshToken(String refreshJWT) throws InvalidRefreshTokenException {
        try {
            String id = JWT.require(algorithm)
                    .build()
                    .verify(refreshJWT)
                    .getId();
            Objects.requireNonNull(id, "jti must be present in refresh token");
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new InvalidRefreshTokenException(e);
        }
    }

    private void checkIfRotated(RefreshToken storedToken) throws InvalidRefreshTokenException {
        // if an old token is used - we still want to invalidate whole chain in case the new one was stolen
        if (storedToken.getNext() != null) {
            String message = "!! INVESTIGATE ASAP !! An old refresh token used for user {}, " +
                    "signifying possible token theft! Invalidating the entire token chain.";
            log.error(message, storedToken.getUser().getUsername());
            refreshTokenRepository.deleteChain(storedToken.getNext());
            throw new InvalidRefreshTokenException();
        }
    }

    private void checkOwner(RefreshToken storedToken, String username) throws InvalidRefreshTokenException {
        User user = storedToken.getUser();
        if (!user.getUsername().equals(username)) {
            // suspend the nasty-ass token pilferer
            String message = "!! INVESTIGATE ASAP !! User {} engaged in a suspicious activity, " +
                    "trying to use a refresh token issued to another user. " +
                    "Blocking the suspicious actor's account pending investigation!";
            log.error(message, username);
            userRepository.changeStatusByEmail(username, UserStatus.SUSPENDED);
            // invalidate token
            refreshTokenRepository.deleteChain(storedToken);
            throw new InvalidRefreshTokenException();
        }
    }
}
