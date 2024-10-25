package com.epam.gymcrmsystemapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class UserExceptions {

    private UserExceptions() {
    }

    public static ResponseStatusException duplicateUsername(String username) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name '" + username + "' already taken");
    }

    public static ResponseStatusException userNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id '" + id + "' not found");
    }

    public static ResponseStatusException userNotFound(String username) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "User with user name '" + username + "' not found");
    }
}
