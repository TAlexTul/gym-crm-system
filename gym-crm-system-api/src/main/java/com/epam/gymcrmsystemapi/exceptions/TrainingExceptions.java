package com.epam.gymcrmsystemapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TrainingExceptions {

    private TrainingExceptions() {
    }

    public static ResponseStatusException trainingNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Training with id '" + id + "' not found");
    }
}
