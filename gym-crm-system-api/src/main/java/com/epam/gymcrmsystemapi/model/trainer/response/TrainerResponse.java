package com.epam.gymcrmsystemapi.model.trainer.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponseForTrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.specialization.response.SpecializationResponse;
import com.epam.gymcrmsystemapi.model.user.UserStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TrainerResponse(String firstName,
                              String lastName,
                              UserStatus status,
                              SpecializationResponse specialization,
                              List<TraineeResponseForTrainerResponse> trainees) {

    public static TrainerResponse fromTrainer(Trainer trainer) {
        return new TrainerResponse(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getStatus(),
                SpecializationResponse.fromSpecialization(trainer.getSpecialization()),
                fromTrainees(trainer.getTrainees())
        );
    }

    public static TrainerResponse fromTrainerWithBasicAttributes(Trainer trainer) {
        return new TrainerResponse(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getStatus(),
                null,
                null
        );
    }

    private static List<TraineeResponseForTrainerResponse> fromTrainees(Set<Trainee> trainees) {
        return trainees.stream()
                .map(TraineeResponseForTrainerResponse::fromTraineeForTrainerResponse)
                .collect(Collectors.toList());
    }
}
