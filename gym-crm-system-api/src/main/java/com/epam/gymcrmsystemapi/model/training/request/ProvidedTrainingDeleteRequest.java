package com.epam.gymcrmsystemapi.model.training.request;

import java.time.OffsetDateTime;

public record ProvidedTrainingDeleteRequest(

        String trainerUsernames,

        OffsetDateTime trainingDate,

        long trainingDuration

) {
}
