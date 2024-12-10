package com.epam.trainerworkloadapi.model;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.model.user.UserStatus;

import java.util.List;

public record MonthlySummaryTrainingsResponse(String trainerUsername,
                                              String trainerFirstName,
                                              String trainerLastName,
                                              UserStatus trainerStatus,
                                              List<ProvidedTrainingResponse> trainings,
                                              Long trainingSummaryDuration) {

    public static MonthlySummaryTrainingsResponse fromEmptyListProvidedTraining(MonthlySummaryTrainingsRequest request) {
        return new MonthlySummaryTrainingsResponse(
                request.trainerUsername(),
                request.trainerFirstName(),
                request.trainerLastName(),
                request.trainerStatus(),
                null,
                null
        );
    }

    public static MonthlySummaryTrainingsResponse fromListProvidedTraining(List<ProvidedTraining> trainings) {
        return new MonthlySummaryTrainingsResponse(
                trainings.get(0).getUser().getUsername(),
                trainings.get(0).getUser().getFirstName(),
                trainings.get(0).getUser().getLastName(),
                trainings.get(0).getUser().getStatus(),
                getProvidedTrainingResponses(trainings),
                calculateTrainingSummaryDuration(trainings)
        );
    }

    private static List<ProvidedTrainingResponse> getProvidedTrainingResponses(List<ProvidedTraining> trainings) {
        return trainings.stream()
                .map(ProvidedTrainingResponse::fromProvidedTraining)
                .toList();
    }

    private static long calculateTrainingSummaryDuration(List<ProvidedTraining> trainings) {
        return trainings.stream()
                .mapToLong(ProvidedTraining::getTrainingDuration)
                .sum();
    }
}
