package com.epam.gymcrmsystemapi.model.trainer.response;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;

public record TrainerRegistrationResponse(Long id,
                                          String username,
                                          String password) {

    public static TrainerRegistrationResponse fromTrainer(Trainer trainer) {
        return new TrainerRegistrationResponse(
                trainer.getId(),
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword()
        );
    }
}
