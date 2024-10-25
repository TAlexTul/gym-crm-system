package com.epam.gymcrmsystemapi.model.user.request;

import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverrideLoginRequestTest {

    private Validator validator;
    private String username;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        username = "John.Doe";
    }

    @Test
    void whenUsernameIsBlank_thenViolationOccurs() {
        var request = new OverrideLoginRequest(
                "",  "validPassword1234", "validPassword123");

        Set<ConstraintViolation<OverrideLoginRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank first name");
        assertEquals("user name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenOldPasswordIsNull_thenViolationOccurs() {
        var request = new OverrideLoginRequest(username,  null, "validPassword123");

        Set<ConstraintViolation<OverrideLoginRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank first name");
        assertEquals("old newPassword must not be null", violations.iterator().next().getMessage());
    }

    @Test
    void testValidPassword() {
        var request = new OverrideLoginRequest(
                username,  "validPassword1234", "validPassword123");

        Set<ConstraintViolation<OverrideLoginRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no validation violations");
    }

    @Test
    void testBlankPassword() {
        var request = new OverrideLoginRequest(username, "validPassword1234", "");

        Set<ConstraintViolation<OverrideLoginRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size(), "Expected two validation violation");
        for (ConstraintViolation<OverrideLoginRequest> violation : violations) {
            String message = violation.getMessage();
            if (message.equals("newPassword must not be blank")) {
                assertEquals("newPassword must not be blank", violation.getMessage());
            } else if (message.equals("newPassword's length must be at least 10")) {
                assertEquals("newPassword's length must be at least 10", violation.getMessage());
            } else {
                throw new AssertionError("Unexpected validation message: " + message);
            }
        }
    }

    @Test
    void testShortPassword() {
        var request = new OverrideLoginRequest(username, "validPassword1234", "short");

        Set<ConstraintViolation<OverrideLoginRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one validation violation");
        ConstraintViolation<OverrideLoginRequest> violation = violations.iterator().next();
        assertEquals("newPassword's length must be at least 10", violation.getMessage());
    }
}
