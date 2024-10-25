package com.epam.gymcrmsystemapi.model.trainee.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeSaveRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidTraineeSaveMergeRequest_thenNoViolations() {
        var request = new TraineeSaveRequest(
                "John",
                "Doe",
                OffsetDateTime.now(),
                "123 Main St"
        );

        Set<ConstraintViolation<TraineeSaveRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenFirstNameIsBlank_thenViolationOccurs() {
        var request = new TraineeSaveRequest(
                "",
                "Doe",
                OffsetDateTime.now(),
                "123 Main St"
        );

        Set<ConstraintViolation<TraineeSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank first name");
        assertEquals("first name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenLastNameIsBlank_thenViolationOccurs() {
        var request = new TraineeSaveRequest(
                "John",
                "",
                OffsetDateTime.now(),
                "123 Main St"
        );

        Set<ConstraintViolation<TraineeSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank last name");
        assertEquals("last name must not be blank", violations.iterator().next().getMessage());
    }
}
