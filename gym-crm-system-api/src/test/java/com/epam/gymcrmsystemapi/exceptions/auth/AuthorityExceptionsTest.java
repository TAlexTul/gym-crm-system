package com.epam.gymcrmsystemapi.exceptions.auth;

import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorityExceptionsTest {

    @Test
    void testAuthorityNotFound() {
        KnownAuthority authority = KnownAuthority.ROLE_ADMIN;

        ResponseStatusException exception = AuthorityExceptions.authorityNotFound(authority.getAuthority());

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("User authority 'ROLE_ADMIN' not defined");
    }

    @Test
    void testInvalidRefreshToken() {
        InvalidRefreshTokenException cause = new InvalidRefreshTokenException(new Exception());

        ResponseStatusException exception = AuthorityExceptions.invalidRefreshToken(cause);

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason())
                .isEqualTo("Refresh token is invalid! It may have been rotated, invalidated or expired naturally");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
