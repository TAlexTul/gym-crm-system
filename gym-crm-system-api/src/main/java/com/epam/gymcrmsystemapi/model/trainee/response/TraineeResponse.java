package com.epam.gymcrmsystemapi.model.trainee.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.user.UserStatus;

import java.time.OffsetDateTime;

public record TraineeResponse(Long userId,
                              String firstName,
                              String lastName,
                              String userName,
                              UserStatus status,
                              Long traineeId,
                              OffsetDateTime dateOfBirth,
                              String address) {

    public static TraineeResponse fromTrainee(Trainee trainee) {
        return new TraineeResponse(
                trainee.getUser().getId(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getUser().getUsername(),
                trainee.getUser().getStatus(),
                trainee.getId(),
                trainee.getDateOfBirth(),
                trainee.getAddress()
        );
    }
}
