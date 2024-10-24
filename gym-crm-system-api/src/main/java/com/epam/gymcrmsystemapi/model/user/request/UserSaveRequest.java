package com.epam.gymcrmsystemapi.model.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserSaveRequest(

        @NotNull(message = "first name must not be null")
        String firstName,

        @NotNull(message = "last name must not be null")
        String lastName,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, message = "password's length must be at least 8")
        String password,

        @NotNull(message = "user name must not be null")
        String username

) {
}
