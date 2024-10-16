package com.epam.gymcrmsystemapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TraineeExceptions {

    private TraineeExceptions() {
    }

    public static ResponseStatusException traineeNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainee with id '" + id + "' not found");
    }

    public static ResponseStatusException traineeNotFound(String firstName, String lastName) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Trainee with first name '" + firstName + "', last name '" + lastName + "' not found");
    }

    public static ResponseStatusException traineeNotFound(String username) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Trainee with user name '" + username + "' not found");
    }
}
