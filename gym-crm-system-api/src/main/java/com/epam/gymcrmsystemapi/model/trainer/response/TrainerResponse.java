package com.epam.gymcrmsystemapi.model.trainer.response;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.user.UserStatus;

public record TrainerResponse(Long userId,
                              String firstName,
                              String lastName,
                              String userName,
                              UserStatus status,
                              Long trainerId,
                              String specialization) {

    public static TrainerResponse fromTrainer(Trainer trainer) {
        return new TrainerResponse(
                trainer.getUser().getId(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUserName(),
                trainer.getUser().getStatus(),
                trainer.getId(),
                trainer.getSpecialization()
        );
    }
}
