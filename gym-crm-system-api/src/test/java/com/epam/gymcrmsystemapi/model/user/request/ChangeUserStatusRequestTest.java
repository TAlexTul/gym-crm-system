package com.epam.gymcrmsystemapi.model.user.request;

import com.epam.gymcrmsystemapi.model.user.UserStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChangeUserStatusRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldThrowConstraintViolationWhenStatusIsNull() {
        ChangeUserStatusRequest request = new ChangeUserStatusRequest(null);

        Set<ConstraintViolation<ChangeUserStatusRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<ChangeUserStatusRequest> violation = violations.iterator().next();
        assertEquals("user status must not be null", violation.getMessage());
        assertEquals("status", violation.getPropertyPath().toString());
    }

    @Test
    void shouldNotThrowConstraintViolationWhenStatusIsNotNull() {
        ChangeUserStatusRequest request = new ChangeUserStatusRequest(UserStatus.ACTIVE);

        Set<ConstraintViolation<ChangeUserStatusRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
