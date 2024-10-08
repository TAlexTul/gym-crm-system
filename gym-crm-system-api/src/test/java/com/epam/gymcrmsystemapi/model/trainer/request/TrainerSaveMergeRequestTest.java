package com.epam.gymcrmsystemapi.model.trainer.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainerSaveMergeRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidTrainerSaveMergeRequest_thenNoViolations() {
        var request = new TrainerSaveMergeRequest(
                "John",
                "Doe",
                "Fitness"
        );

        Set<ConstraintViolation<TrainerSaveMergeRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenFirstNameIsBlank_thenViolationOccurs() {
        var request = new TrainerSaveMergeRequest(
                "",
                "Doe",
                "Fitness"
        );

        Set<ConstraintViolation<TrainerSaveMergeRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank first name");
        assertEquals("first name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenLastNameIsBlank_thenViolationOccurs() {
        var request = new TrainerSaveMergeRequest(
                "John",
                "",
                "Fitness"
        );

        Set<ConstraintViolation<TrainerSaveMergeRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank last name");
        assertEquals("last name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenSpecializationIsBlank_thenViolationOccurs() {
        var request = new TrainerSaveMergeRequest(
                "John",
                "Doe",
                ""
        );

        Set<ConstraintViolation<TrainerSaveMergeRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank specialization");
        assertEquals("specialization must not be blank", violations.iterator().next().getMessage());
    }
}
