package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.training.type.Type;
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
                "John.Doe",
                "Jane.Jenkins",
                "Training 1",
                Type.STRENGTH_TRAINING,
                OffsetDateTime.now(),
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void whenTraineeUsernameIsBlank_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "",
                "Jane.Jenkins",
                "Training 1",
                Type.FUNCTIONAL_TRAINING,
                OffsetDateTime.now(),
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for null training type");
        assertEquals("trainee user name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenTrainerUsernameIsBlank_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "John.Doe",
                "",
                "Training 1",
                Type.FUNCTIONAL_TRAINING,
                OffsetDateTime.now(),
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for null training type");
        assertEquals("trainer user name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenTrainingNameIsBlank_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "John.Doe",
                "Jane.Jenkins",
                "",
                Type.STRENGTH_TRAINING,
                OffsetDateTime.now(),
                300000L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for blank training name");
        assertEquals("training name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenTrainingDateIsNull_thenViolationOccurs() {
        var request = new TrainingSaveRequest(
                "John.Doe",
                "Jane.Jenkins",
                "Training 1",
                Type.STRENGTH_TRAINING,
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
                "John.Doe",
                "Jane.Jenkins",
                "Training 1",
                Type.STRENGTH_TRAINING,
                OffsetDateTime.now(),
                0L
        );

        Set<ConstraintViolation<TrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "Expected one violation for null training duration");
        assertEquals("training duration must be positive", violations.iterator().next().getMessage());
    }
}
