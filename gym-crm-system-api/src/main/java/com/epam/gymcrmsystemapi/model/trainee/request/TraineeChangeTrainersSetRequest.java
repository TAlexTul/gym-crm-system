package com.epam.gymcrmsystemapi.model.trainee.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TraineeChangeTrainersSetRequest(

        @NotBlank(message = "trainee user name must not be blank")
        String traineeUsername,

        @Valid
        @NotNull(message = "trainer`s user names list must not be null")
        List<
                @NotBlank(message = "trainer`s user names must not be blank")
                        String> trainerUsernames

) {
}
