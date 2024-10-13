package com.epam.gymcrmsystemapi.model.training.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public record TrainingResponse(Long id,
                               Set<Trainee> trainees,
                               Set<Trainer> trainers,
                               String trainingName,
                               List<TrainingType> trainingTypes,
                               OffsetDateTime trainingDate,
                               Long trainingDuration) {

    public static TrainingResponse fromTraining(Training training) {
        return new TrainingResponse(
                training.getId(),
                training.getTrainees(),
                training.getTrainers(),
                training.getTrainingName(),
                training.getTrainingTypes(),
                training.getTrainingDate(),
                training.getTrainingDuration()
        );
    }

    public static TrainingResponse fromTrainingWithBasicAttributes(Training training) {
        return new TrainingResponse(
                training.getId(),
                null,
                null,
                training.getTrainingName(),
                training.getTrainingTypes(),
                training.getTrainingDate(),
                training.getTrainingDuration()
        );
    }
}
