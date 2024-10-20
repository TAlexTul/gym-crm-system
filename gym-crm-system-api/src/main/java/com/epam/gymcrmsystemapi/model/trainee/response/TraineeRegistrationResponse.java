package com.epam.gymcrmsystemapi.model.trainee.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;

public record TraineeRegistrationResponse(Long id,
                                          String userName,
                                          String password) {

    public static TraineeRegistrationResponse fromTrainee(Trainee trainee) {
        return new TraineeRegistrationResponse(
                trainee.getId(),
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword()
        );
    }
}
