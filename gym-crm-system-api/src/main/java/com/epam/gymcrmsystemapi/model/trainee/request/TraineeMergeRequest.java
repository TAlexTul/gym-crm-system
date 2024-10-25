package com.epam.gymcrmsystemapi.model.trainee.request;

import com.epam.gymcrmsystemapi.model.user.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record TraineeMergeRequest(

        @NotBlank(message = "user name must not be blank")
        String username,

        @NotBlank(message = "first name must not be blank")
        String firstName,

        @NotBlank(message = "last name must not be blank")
        String lastName,

        OffsetDateTime dateOfBirth,

        String address,

        @NotNull(message = "user status must not null")
        UserStatus status

) {
}
