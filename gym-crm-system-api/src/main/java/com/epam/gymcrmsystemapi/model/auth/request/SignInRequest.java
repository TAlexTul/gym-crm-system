package com.epam.gymcrmsystemapi.model.auth.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record SignInRequest(

        @JsonAlias({"username"})
        String login,

        String password

) {
}
