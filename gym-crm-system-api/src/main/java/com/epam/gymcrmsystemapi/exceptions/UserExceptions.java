package com.epam.gymcrmsystemapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class UserExceptions {

    private UserExceptions() {
    }

    public static ResponseStatusException duplicateUsername(String username) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name '" + username + "' already taken");
    }
}
