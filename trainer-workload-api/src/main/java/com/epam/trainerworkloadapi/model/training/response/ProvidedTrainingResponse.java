package com.epam.trainerworkloadapi.model.training.response;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;

import java.time.Month;

public record ProvidedTrainingResponse(Long id,
                                       Integer year,
                                       Month month,
                                       Long trainingDuration) {

    public static ProvidedTrainingResponse fromProvidedTraining(ProvidedTraining training) {
        return new ProvidedTrainingResponse(
                training.getId(),
                training.getTrainingDate().getYear(),
                training.getTrainingDate().getMonth(),
                training.getTrainingDuration()
        );
    }
}
