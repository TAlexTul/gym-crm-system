package com.epam.gymcrmsystemapi.model.auth.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RefreshTokenRequestTest {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WhenRefreshTokenIsProvided() {
        String validToken = "refresh-token";
        RefreshTokenRequest request = new RefreshTokenRequest(validToken);

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenRefreshTokenIsNull() {
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank first name");
        assertEquals("refresh token must not be null", violations.iterator().next().getMessage());
    }
}
