package com.epam.trainerworkloadapi.service.summary;

import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.summary.response.SummaryTrainingsDurationResponse;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;

import java.time.OffsetDateTime;

public interface SummaryOperations {

    SummaryTrainingsDurationResponse create(ProvidedTrainingSaveRequest request);

    void deleteProvidedTrainings(String trainerUsernames, OffsetDateTime trainingDate, long trainingDuration);

    MonthlySummaryTrainingsResponse getMonthlySummaryTrainingsDuration(MonthlySummaryTrainingsRequest request);

}
