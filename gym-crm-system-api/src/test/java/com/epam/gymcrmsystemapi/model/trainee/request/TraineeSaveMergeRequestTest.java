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

class TraineeSaveMergeRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidTraineeSaveMergeRequest_thenNoViolations() {
        var request = new TraineeSaveMergeRequest(
                "John",
                "Doe",
                OffsetDateTime.now(),
                "123 Main St"
        );

        Set<ConstraintViolation<TraineeSaveMergeRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenFirstNameIsBlank_thenViolationOccurs() {
        var request = new TraineeSaveMergeRequest(
                "",
                "Doe",
                OffsetDateTime.now(),
                "123 Main St"
        );

        Set<ConstraintViolation<TraineeSaveMergeRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank first name");
        assertEquals("first name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenLastNameIsBlank_thenViolationOccurs() {
        var request = new TraineeSaveMergeRequest(
                "John",
                "",
                OffsetDateTime.now(),
                "123 Main St"
        );

        Set<ConstraintViolation<TraineeSaveMergeRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank last name");
        assertEquals("last name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenDateOfBirthIsNull_thenViolationOccurs() {
        var request = new TraineeSaveMergeRequest(
                "John",
                "Doe",
                null,
                "123 Main St"
        );

        Set<ConstraintViolation<TraineeSaveMergeRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for null date of birth");
        assertEquals("date of birth must not be null", violations.iterator().next().getMessage());
    }

    @Test
    void whenAddressIsBlank_thenViolationOccurs() {
        var request = new TraineeSaveMergeRequest(
                "John",
                "Doe",
                OffsetDateTime.now(),
                ""
        );

        Set<ConstraintViolation<TraineeSaveMergeRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank address");
        assertEquals("address must not be blank", violations.iterator().next().getMessage());
    }
}
