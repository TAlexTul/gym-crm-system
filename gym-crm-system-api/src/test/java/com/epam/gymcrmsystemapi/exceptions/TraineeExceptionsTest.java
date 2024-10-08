package com.epam.gymcrmsystemapi.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraineeExceptionsTest {

    @Test
    void testTraineeNotFound_Id() {
        long id = 123;

        ResponseStatusException rse = TraineeExceptions.traineeNotFound(id);

        String message = rse.getMessage();
        String[] parts = message.split(" ", 3);
        int statusCode = Integer.parseInt(parts[0]);
        HttpStatus status = HttpStatus.valueOf(statusCode);

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainee with id '123' not found", rse.getReason());
    }

    @Test
    void testTraineeNotFound_FirstNameLastName() {
        String firstName = "John";
        String lastName = "Doe";

        ResponseStatusException rse = TraineeExceptions.traineeNotFound(firstName, lastName);

        String message = rse.getMessage();
        String[] parts = message.split(" ", 3);
        int statusCode = Integer.parseInt(parts[0]);
        HttpStatus status = HttpStatus.valueOf(statusCode);

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainee with first name 'John', last name 'Doe' not found", rse.getReason());
    }
}
