package com.epam.gymcrmsystemapi.exceptions.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InvalidRefreshTokenExceptionTest {

    @Test
    void testDefaultConstructor() {
        InvalidRefreshTokenException exception = new InvalidRefreshTokenException();
        assertThat(exception).isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Test cause");
        InvalidRefreshTokenException exception = new InvalidRefreshTokenException(cause);
        assertThat(exception).isInstanceOf(InvalidRefreshTokenException.class);
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
