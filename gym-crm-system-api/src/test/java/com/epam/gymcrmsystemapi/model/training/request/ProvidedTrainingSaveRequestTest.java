package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.user.UserStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProvidedTrainingSaveRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        ProvidedTrainingSaveRequest request = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                OffsetDateTime.now(),
                60
        );

        Set<jakarta.validation.ConstraintViolation<ProvidedTrainingSaveRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldFailValidationWhenTrainerUsernameIsNull() {
        ProvidedTrainingSaveRequest request = new ProvidedTrainingSaveRequest(
                null,
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                OffsetDateTime.now(),
                60
        );

        Set<jakarta.validation.ConstraintViolation<ProvidedTrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("trainer username must not be null", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenTrainingDurationIsNegative() {
        ProvidedTrainingSaveRequest request = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                OffsetDateTime.now(),
                -10
        );

        Set<jakarta.validation.ConstraintViolation<ProvidedTrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("training duration must be positive", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenMultipleFieldsAreInvalid() {
        ProvidedTrainingSaveRequest request = new ProvidedTrainingSaveRequest(
                null,
                null,
                "Jenkins",
                null,
                null,
                -10
        );

        Set<jakarta.validation.ConstraintViolation<ProvidedTrainingSaveRequest>> violations = validator.validate(request);

        assertEquals(5, violations.size());
    }
}
