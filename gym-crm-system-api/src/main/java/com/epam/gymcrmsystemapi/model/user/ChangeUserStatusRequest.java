package com.epam.gymcrmsystemapi.model.user;

import jakarta.validation.constraints.NotNull;

public record ChangeUserStatusRequest(

        @NotNull(message = "user status must not be null")
        UserStatus status

) {
}
