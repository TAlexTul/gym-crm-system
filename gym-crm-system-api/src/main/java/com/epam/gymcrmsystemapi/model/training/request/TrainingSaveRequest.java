package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.training.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record TrainingSaveRequest(

        String traineeUsername,

        String trainerUsername,

        @NotBlank(message = "training name must not be blank")
        String trainingName,

        @NotNull(message = "training type must not be null")
        TrainingType trainingType,

        @NotNull(message = "training date must not be null")
        OffsetDateTime trainingDate,

        @Positive(message = "training duration must be positive")
        long trainingDuration

) {
}
