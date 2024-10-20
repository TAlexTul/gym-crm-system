package com.epam.gymcrmsystemapi.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerExceptionsTest {

    @Test
    void testTrainerNotFound_Id() {
        long id = 123;

        ResponseStatusException rse = TrainerExceptions.trainerNotFound(id);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainer with id '123' not found", rse.getReason());
    }

    @Test
    void testTrainerNotFound_FirstNameLastName() {
        String firstName = "John";
        String lastName = "Doe";

        ResponseStatusException rse = TrainerExceptions.trainerNotFound(firstName, lastName);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainer with first name 'John', last name 'Doe' not found", rse.getReason());
    }

    @Test
    void testTrainerNotFound_Username() {
        String username = "John.Doe";

        ResponseStatusException rse = TrainerExceptions.trainerNotFound(username);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Trainer with user name 'John.Doe' not found", rse.getReason());
    }

    @Test
    void testUsernameAlreadyRegistered() {
        String username = "John.Doe";

        ResponseStatusException rse = TrainerExceptions.usernameAlreadyRegistered(username);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.BAD_REQUEST, status);
        assertEquals("User name 'John.Doe' already registered as trainee", rse.getReason());
    }

    @Test
    void testDuplicateUsername() {
        String username = "John.Doe";

        ResponseStatusException rse = TrainerExceptions.duplicateUsername(username);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.BAD_REQUEST, status);
        assertEquals("User name 'John.Doe' already taken", rse.getReason());
    }

    @Test
    void testWrongPassword() {
        ResponseStatusException rse = TrainerExceptions.wrongPassword();

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.BAD_REQUEST, status);
        assertEquals("Password is incorrect", rse.getReason());
    }
}
