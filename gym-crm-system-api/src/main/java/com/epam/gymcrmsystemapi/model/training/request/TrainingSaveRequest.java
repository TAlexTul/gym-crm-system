package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.training.type.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record TrainingSaveRequest(

        @NotBlank(message = "trainee user name must not be blank")
        String traineeUsername,

        @NotBlank(message = "trainer user name must not be blank")
        String trainerUsername,

        @NotBlank(message = "training name must not be blank")
        String trainingName,

        Type trainingType,

        @NotNull(message = "training date must not be null")
        OffsetDateTime trainingDate,

        @Positive(message = "training duration must be positive")
        long trainingDuration

) {
}
