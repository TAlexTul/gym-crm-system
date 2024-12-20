package com.epam.trainerworkloadapi.model;

import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;

import java.util.List;

public record MonthlySummaryTrainingsResponse(String trainerUsername,
                                              String trainerFirstName,
                                              String trainerLastName,
                                              UserStatus trainerStatus,
                                              List<ProvidedTraining> trainings,
                                              long trainingSummaryDuration) {

    public static MonthlySummaryTrainingsResponse fromSummaryTrainingsDuration(SummaryTrainingsDuration std) {
        return new MonthlySummaryTrainingsResponse(
                std.getUsername(),
                std.getFirstName(),
                std.getLastName(),
                std.getUserStatus(),
                std.getTrainings(),
                std.getSummaryTrainingsDuration()
        );
    }
}
