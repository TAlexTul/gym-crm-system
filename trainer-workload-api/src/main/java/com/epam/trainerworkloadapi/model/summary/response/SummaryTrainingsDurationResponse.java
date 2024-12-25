package com.epam.trainerworkloadapi.model.summary.response;

import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;

import java.util.List;

public record SummaryTrainingsDurationResponse(String id,
                                               String trainerUsername,
                                               String trainerFirstName,
                                               String trainerLastName,
                                               UserStatus trainerStatus,
                                               List<ProvidedTraining> providedTrainings,
                                               Long summaryTrainingsDuration) {

    public static SummaryTrainingsDurationResponse formSummaryTrainingsDuration(SummaryTrainingsDuration duration) {
        return new SummaryTrainingsDurationResponse(
                duration.getId(),
                duration.getUsername(),
                duration.getFirstName(),
                duration.getLastName(),
                duration.getUserStatus(),
                duration.getTrainings(),
                duration.getSummaryTrainingsDuration()
        );
    }
}
