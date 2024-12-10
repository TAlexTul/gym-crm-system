package com.epam.trainerworkloadapi.service;

import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;

import java.time.OffsetDateTime;
import java.time.YearMonth;

public interface TrainingOperations {

    ProvidedTrainingResponse create(ProvidedTrainingSaveRequest request);

    void deleteProvidedTrainings(String trainerUsername, OffsetDateTime trainingDate, long trainingDuration);

    void deleteTrainingsByYearMonth(YearMonth yearMonth);

    MonthlySummaryTrainingsResponse findSummaryTrainings(MonthlySummaryTrainingsRequest request);

}
