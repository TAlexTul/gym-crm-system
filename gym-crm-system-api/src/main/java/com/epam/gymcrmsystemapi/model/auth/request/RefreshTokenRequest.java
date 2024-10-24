package com.epam.gymcrmsystemapi.model.auth.request;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(

        @NotNull(message = "refresh token must not be null")
        String refreshToken

) {
}
