package com.epam.gymcrmsystemapi.model.trainer.response;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.specialization.response.SpecializationResponse;

public record TrainerResponseForTraineeResponse(String username,
                                                String firstName,
                                                String lastName,
                                                SpecializationResponse specialization) {

    public static TrainerResponseForTraineeResponse fromTrainerForTraineeResponse(Trainer trainer) {
        return new TrainerResponseForTraineeResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                SpecializationResponse.fromSpecialization(trainer.getSpecialization())
        );
    }
}
