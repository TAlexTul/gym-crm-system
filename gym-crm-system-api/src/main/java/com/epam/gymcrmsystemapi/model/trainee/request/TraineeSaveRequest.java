package com.epam.gymcrmsystemapi.model.trainee.request;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record TraineeSaveRequest(

        @NotBlank(message = "first name must not be blank")
        String firstName,

        @NotBlank(message = "last name must not be blank")
        String lastName,

        OffsetDateTime dateOfBirth,

        String address

) {
}
