package com.epam.gymcrmsystemapi.model.training.request;

import com.epam.gymcrmsystemapi.model.user.UserStatus;

import java.time.OffsetDateTime;

public record ProvidedTrainingSaveRequest(

        String trainerUsername,

        String trainerFirstName,

        String trainerLastName,

        UserStatus trainerStatus,

        OffsetDateTime trainingDate,

        long trainingDuration

) {
}
