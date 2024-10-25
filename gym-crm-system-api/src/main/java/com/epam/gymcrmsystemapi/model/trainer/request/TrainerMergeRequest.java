package com.epam.gymcrmsystemapi.model.trainer.request;

import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerMergeRequest(

        @NotBlank(message = "user name must not be blank")
        String username,

        @NotBlank(message = "first name must not be blank")
        String firstName,

        @NotBlank(message = "last name must not be blank")
        String lastName,

        @NotNull(message = "user status must not null")
        UserStatus status,

        @NotNull(message = "specialization must not null")
        Specialization specialization

) {
}
