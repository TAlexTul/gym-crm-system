package com.epam.gymcrmsystemapi.model.trainer.request;

import jakarta.validation.constraints.NotBlank;

public record TrainerSaveMergeRequest(

        @NotBlank(message = "first name must not be blank")
        String firstName,

        @NotBlank(message = "last name must not be blank")
        String lastName,

        @NotBlank(message = "specialization must not be blank")
        String specialization

) {
}
