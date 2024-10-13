package com.epam.gymcrmsystemapi.model.user.request;

import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverridePasswordRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidPassword() {
        var request = new OverridePasswordRequest("validPassword123");

        Set<ConstraintViolation<OverridePasswordRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no validation violations");
    }

    @Test
    void testBlankPassword() {
        var request = new OverridePasswordRequest("");

        Set<ConstraintViolation<OverridePasswordRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size(), "Expected two validation violation");
        for (ConstraintViolation<OverridePasswordRequest> violation : violations) {
            String message = violation.getMessage();
            if (message.equals("password must not be blank")) {
                assertEquals("password must not be blank", violation.getMessage());
            } else if (message.equals("password's length must be at least 10")) {
                assertEquals("password's length must be at least 10", violation.getMessage());
            } else {
                throw new AssertionError("Unexpected validation message: " + message);
            }
        }
    }

    @Test
    void testShortPassword() {
        var request = new OverridePasswordRequest("short");

        Set<ConstraintViolation<OverridePasswordRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one validation violation");
        ConstraintViolation<OverridePasswordRequest> violation = violations.iterator().next();
        assertEquals("password's length must be at least 10", violation.getMessage());
    }
}
