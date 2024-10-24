package com.epam.gymcrmsystemapi.model.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OverrideLoginRequest(

        @NotBlank(message = "user name must not be blank")
        String username,

        @NotNull(message = "old newPassword must not be null")
        String oldPassword,

        @NotBlank(message = "newPassword must not be blank")
        @Size(min = 10, message = "newPassword's length must be at least 10")
        String newPassword

) {
}
