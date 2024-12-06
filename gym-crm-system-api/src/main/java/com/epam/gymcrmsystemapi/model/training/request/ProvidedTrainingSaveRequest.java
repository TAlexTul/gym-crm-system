package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.user.UserStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record ProvidedTrainingSaveRequest(

        @NotNull(message = "trainer username must not be null")
        String trainerUsername,

        @NotNull(message = "trainer first name must not be null")
        String trainerFirstName,

        @NotNull(message = "trainer last name must not be null")
        String trainerLastName,

        @NotNull(message = "trainer user status must not be null")
        UserStatus trainerStatus,

        @NotNull(message = "trainer training date must not be null")
        OffsetDateTime trainingDate,

        @Positive(message = "training duration must be positive")
        long trainingDuration

) {
}
