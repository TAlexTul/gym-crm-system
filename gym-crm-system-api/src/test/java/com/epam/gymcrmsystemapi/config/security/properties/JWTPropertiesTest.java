package com.epam.gymcrmsystemapi.config.security.properties;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JWTPropertiesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSecretNotEmpty() {
        JWTProperties jwtProperties = new JWTProperties();

        assertThatThrownBy(() -> {
            jwtProperties.setSecret("");
            Set<ConstraintViolation<JWTProperties>> violations = validator.validate(jwtProperties);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("secret must not be empty");
    }

    @Test
    void testAccessExpireInDurationLimits() {
        JWTProperties jwtProperties = new JWTProperties();
        jwtProperties.setSecret("mySecret");

        assertThatThrownBy(() -> {
            jwtProperties.setAccessExpireIn(Duration.ofMinutes(31));
            Set<ConstraintViolation<JWTProperties>> violations = validator.validate(jwtProperties);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be less than or equal to 30 minutes");

        assertThatThrownBy(() -> {
            jwtProperties.setAccessExpireIn(Duration.ofMinutes(0));
            Set<ConstraintViolation<JWTProperties>> violations = validator.validate(jwtProperties);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be greater than or equal to 1 minute");
    }

    @Test
    void testRefreshExpireInDurationLimits() {
        JWTProperties jwtProperties = new JWTProperties();
        jwtProperties.setSecret("mySecret");

        assertThatThrownBy(() -> {
            jwtProperties.setRefreshExpireIn(Duration.ofDays(8));
            Set<ConstraintViolation<JWTProperties>> violations = validator.validate(jwtProperties);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("must be less than or equal to 7 days");

        assertThatThrownBy(() -> {
            jwtProperties.setRefreshExpireIn(Duration.ofHours(11));
            Set<ConstraintViolation<JWTProperties>> violations = validator.validate(jwtProperties);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("must be greater than or equal to 12 hours");
    }

    @Test
    void testValidJWTProperties() {
        JWTProperties jwtProperties = new JWTProperties();
        jwtProperties.setSecret("mySecret");
        jwtProperties.setAccessExpireIn(Duration.ofMinutes(15));
        jwtProperties.setRefreshExpireIn(Duration.ofDays(5));

        Set<ConstraintViolation<JWTProperties>> violations = validator.validate(jwtProperties);

        assertThat(violations).isEmpty();
    }
}
