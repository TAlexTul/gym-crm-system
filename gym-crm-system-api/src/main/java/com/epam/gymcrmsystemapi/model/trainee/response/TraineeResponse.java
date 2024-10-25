package com.epam.gymcrmsystemapi.model.trainee.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TraineeResponse(String firstName,
                              String lastName,
                              UserStatus status,
                              @JsonFormat(shape = JsonFormat.Shape.STRING)
                              OffsetDateTime dateOfBirth,
                              String address,
                              List<TrainerResponse> trainers) {

    public static TraineeResponse fromTrainee(Trainee trainee) {
        trainee.getTrainers()
                .forEach(t -> t.setTrainees(new HashSet<>()));
        return new TraineeResponse(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getUser().getStatus(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                fromTrainers(trainee.getTrainers())
        );
    }

    public static TraineeResponse fromTraineeWithBasicAttribute(Trainee trainee) {
        return new TraineeResponse(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getUser().getStatus(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                null
        );
    }

    private static List<TrainerResponse> fromTrainers(Set<Trainer> trainers) {
        return trainers.stream()
                .map(TrainerResponse::fromTrainer)
                .collect(Collectors.toList());
    }
}
