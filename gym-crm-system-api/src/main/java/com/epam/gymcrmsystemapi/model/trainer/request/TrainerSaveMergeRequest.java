package com.epam.gymcrmsystemapi.model.trainer.request;

import com.epam.gymcrmsystemapi.model.trainer.Specialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerSaveMergeRequest(

        @NotBlank(message = "first name must not be blank")
        String firstName,

        @NotBlank(message = "last name must not be blank")
        String lastName,

        @NotNull(message = "specialization must not be null")
        Specialization specialization

) {
}
