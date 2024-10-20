package com.epam.gymcrmsystemapi.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingExceptionsTest {

    @Test
    void testTrainingNotFound_Id() {
        long id = 123;

        ResponseStatusException rse = TrainingExceptions.trainingNotFound(id);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Training with id '123' not found", rse.getReason());
    }
}
