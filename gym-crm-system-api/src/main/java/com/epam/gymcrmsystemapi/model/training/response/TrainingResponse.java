package com.epam.gymcrmsystemapi.model.training.response;

import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;

import java.time.Duration;
import java.time.OffsetDateTime;

public record TrainingResponse(Long id,
                               String traineeFirstName,
                               String traineeLastName,
                               String traineeNickName,
                               String trainerUserName,
                               String trainerLastName,
                               String trainerNickName,
                               String trainingName,
                               TrainingType trainingType,
                               OffsetDateTime trainingDate,
                               Duration trainingDuration) {

    public static TrainingResponse fromTraining(Training training) {
        return new TrainingResponse(
                training.getId(),
                training.getTrainee().getUser().getFirstName(),
                training.getTrainee().getUser().getLastName(),
                training.getTrainee().getUser().getUserName(),
                training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName(),
                training.getTrainer().getUser().getUserName(),
                training.getTrainingName(),
                training.getTrainingType(),
                training.getTrainingDate(),
                training.getTrainingDuration()
        );
    }

    public static TrainingResponse fromTrainingWithBasicAttributes(Training training) {
        return new TrainingResponse(
                training.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                training.getTrainingName(),
                training.getTrainingType(),
                training.getTrainingDate(),
                training.getTrainingDuration()
        );
    }
}
