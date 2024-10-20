package com.epam.gymcrmsystemapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TrainerExceptions {

    private TrainerExceptions() {
    }

    public static ResponseStatusException trainerNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer with id '" + id + "' not found");
    }

    public static ResponseStatusException trainerNotFound(String firstName, String lastName) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Trainer with first name '" + firstName + "', last name '" + lastName + "' not found");
    }

    public static ResponseStatusException trainerNotFound(String username) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Trainer with user name '" + username + "' not found");
    }

    public static ResponseStatusException usernameAlreadyRegistered(String username) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name '" + username + "' already registered as trainee");
    }

    public static ResponseStatusException duplicateUsername(String username) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name '" + username + "' already taken");
    }

    public static ResponseStatusException wrongPassword() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect");
    }
}
