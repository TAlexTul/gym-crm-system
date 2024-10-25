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

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainee with id '123' not found", rse.getReason());
    }

    @Test
    void testTraineeNotFound_FirstNameLastName() {
        String firstName = "John";
        String lastName = "Doe";

        ResponseStatusException rse = TraineeExceptions.traineeNotFound(firstName, lastName);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainee with first name 'John', last name 'Doe' not found", rse.getReason());
    }

    @Test
    void testTraineeNotFound_Username() {
        String username = "John.Doe";

        ResponseStatusException rse = TraineeExceptions.traineeNotFound(username);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainee with user name 'John.Doe' not found", rse.getReason());
    }

    @Test
    void testUsernameAlreadyRegistered() {
        String username = "John.Doe";

        ResponseStatusException rse = TraineeExceptions.usernameAlreadyRegistered(username);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.BAD_REQUEST, status);
        assertEquals("User name 'John.Doe' already registered as trainer", rse.getReason());
    }

    @Test
    void testDuplicateUsername() {
        String username = "John.Doe";

        ResponseStatusException rse = TraineeExceptions.duplicateUsername(username);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.BAD_REQUEST, status);
        assertEquals("User name 'John.Doe' already taken", rse.getReason());
    }

    @Test
    void testWrongPassword() {
        ResponseStatusException rse = TraineeExceptions.wrongPassword();

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.BAD_REQUEST, status);
        assertEquals("Password is incorrect", rse.getReason());
    }
}
