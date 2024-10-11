package com.epam.gymcrmsystemapi.model.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OverridePasswordRequest(

        @NotBlank(message = "password must not be blank")
        @Size(min = 10, message = "password's length must be at least 10")
        String password

) {
}
