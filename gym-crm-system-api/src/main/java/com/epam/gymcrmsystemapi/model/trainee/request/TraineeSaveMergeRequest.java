package com.epam.gymcrmsystemapi.model.trainee.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record TraineeSaveMergeRequest(

        @NotBlank(message = "first name must not be blank")
        String firstName,

        @NotBlank(message = "last name must not be blank")
        String lastName,

        @NotNull(message = "date of birth must not be null")
        OffsetDateTime dateOfBirth,

        @NotBlank(message = "address must not be blank")
        String address

) {
}
