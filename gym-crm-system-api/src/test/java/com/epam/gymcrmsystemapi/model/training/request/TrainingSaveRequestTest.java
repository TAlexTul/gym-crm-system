package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.training.Type;
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

class TrainingSaveRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidTrainingSaveRequest_thenNoViolations() {
        var request = new TrainingSaveRequest(
                "John",
                "Doe",
                "Training 1",
                new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING),
                OffsetDateTime.now(),
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenTrainingNameIsBlank_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "John",
                "Doe",
                "",
                new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING),
                OffsetDateTime.now(),
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank training name");
        assertEquals("training name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenTrainingTypeIsNull_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "John",
                "Doe",
                "Training 1",
                null,
                OffsetDateTime.now(),
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for null training type");
        assertEquals("training type must not be null", violations.iterator().next().getMessage());
    }

    @Test
    void whenTrainingDateIsNull_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "John",
                "Doe",
                "Training 1",
                new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING),
                null,
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for null training date");
        assertEquals("training date must not be null", violations.iterator().next().getMessage());
    }

    @Test
    void whenTrainingDurationIsNull_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "John",
                "Doe",
                "Training 1",
                new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING),
                OffsetDateTime.now(),
                0L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for null training duration");
        assertEquals("training duration must be positive", violations.iterator().next().getMessage());
    }
}
