package com.epam.gymcrmsystemapi.model.auth;

import com.fasterxml.jackson.annotation.JsonAlias;

public record SignInRequest(

        @JsonAlias({"username"})
        String login,
        String password

) {
}
