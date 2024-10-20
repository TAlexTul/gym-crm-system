package com.epam.gymcrmsystemapi.model.trainer.request;

import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainerSaveRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidTrainerSaveMergeRequest_thenNoViolations() {
        var request = new TrainerSaveRequest(
                "John",
                "Doe",
                SpecializationType.PERSONAL_TRAINER
        );

        Set<ConstraintViolation<TrainerSaveRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenFirstNameIsBlank_thenViolationOccurs() {
        var request = new TrainerSaveRequest(
                "",
                "Doe",
                SpecializationType.PERSONAL_TRAINER
        );

        Set<ConstraintViolation<TrainerSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank first name");
        assertEquals("first name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenLastNameIsBlank_thenViolationOccurs() {
        var request = new TrainerSaveRequest(
                "John",
                "",
                SpecializationType.PERSONAL_TRAINER
        );

        Set<ConstraintViolation<TrainerSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank last name");
        assertEquals("last name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenSpecializationIsNull_thenViolationOccurs() {
        var request = new TrainerSaveRequest(
                "John",
                "Doe",
                null
        );

        Set<ConstraintViolation<TrainerSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank specialization");
        assertEquals("specialization must not be null", violations.iterator().next().getMessage());
    }
}
