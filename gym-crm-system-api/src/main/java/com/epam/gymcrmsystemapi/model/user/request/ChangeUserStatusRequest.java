package com.epam.gymcrmsystemapi.model.user.request;

import com.epam.gymcrmsystemapi.model.user.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeUserStatusRequest(

        @NotNull(message = "user status must not be null")
        UserStatus status

) {
}
