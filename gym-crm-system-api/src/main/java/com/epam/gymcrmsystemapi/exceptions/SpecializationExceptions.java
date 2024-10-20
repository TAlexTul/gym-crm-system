package com.epam.gymcrmsystemapi.exceptions;

import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class SpecializationExceptions {

    private SpecializationExceptions() {
    }

    public static ResponseStatusException specializationNotFound(SpecializationType id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialization with id '" + id + "' not found");
    }
}
