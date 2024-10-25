package com.epam.gymcrmsystemapi.model.training.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.response.TrainingTypeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TrainingResponse(Long id,
                               Set<TraineeResponse> trainees,
                               Set<TrainerResponse> trainers,
                               String trainingName,
                               List<TrainingTypeResponse> trainingTypes,
                               @JsonFormat(shape = JsonFormat.Shape.STRING)
                               OffsetDateTime trainingDate,
                               Long trainingDuration) {

    public static TrainingResponse fromTraining(Training training) {
        return new TrainingResponse(
                training.getId(),
                getTraineeResponses(training.getTrainees()),
                getTrainerResponses(training.getTrainers()),
                training.getTrainingName(),
                getTrainingTypes(training.getTrainingTypes()),
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
                null,
                training.getTrainingDate(),
                training.getTrainingDuration()
        );
    }

    private static Set<TraineeResponse> getTraineeResponses(Set<Trainee> trainees) {
        return trainees.stream()
                .map(TraineeResponse::fromTrainee)
                .collect(Collectors.toSet());
    }

    private static Set<TrainerResponse> getTrainerResponses(Set<Trainer> trainers) {
        return trainers.stream()
                .map(TrainerResponse::fromTrainer)
                .collect(Collectors.toSet());
    }

    private static List<TrainingTypeResponse> getTrainingTypes(List<TrainingType> trainingTypes) {
        return trainingTypes.stream()
                .map(TrainingTypeResponse::fromTrainingType)
                .toList();
    }
}
