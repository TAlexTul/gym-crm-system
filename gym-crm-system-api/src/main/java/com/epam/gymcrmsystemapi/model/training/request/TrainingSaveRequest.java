package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.training.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Duration;
import java.time.OffsetDateTime;

public record TrainingSaveRequest(

        @Positive(message = "trainee traineeId must be positive")
        long traineeId,

        @Positive(message = "trainer traineeId must be positive")
        long trainerId,

        @NotBlank(message = "training name must not be blank")
        String trainingName,

        @NotNull(message = "training type must not be null")
        TrainingType trainingType,

        @NotNull(message = "training date must not be null")
        OffsetDateTime trainingDate,

        @NotNull(message = "training duration must not be null")
        Duration trainingDuration

) {
}
