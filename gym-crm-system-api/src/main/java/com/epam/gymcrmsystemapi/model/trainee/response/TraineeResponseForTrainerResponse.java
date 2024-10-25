package com.epam.gymcrmsystemapi.model.trainee.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;

public record TraineeResponseForTrainerResponse(String username,
                                                String firstName,
                                                String lastName) {

    public static TraineeResponseForTrainerResponse fromTraineeForTrainerResponse(Trainee trainee) {
        return new TraineeResponseForTrainerResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName()
        );
    }
}
