package com.epam.gymcrmsystemapi.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserExceptionsTest {

    @Test
    void testDuplicateFirstNameAndLastName() {
        String firstName = "John";
        String lastName = "Doe";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            throw UserExceptions.duplicateFirstNameAndLastName(firstName, lastName);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("User with first name 'John', last name 'Doe' is already taken", exception.getReason());
    }

    @Test
    void testUserNotFoundById() {
        long userId = 1L;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            throw UserExceptions.userNotFound(userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User with id '1' not found", exception.getReason());
    }

    @Test
    void testUserNotFoundByUsername() {
        String username = "johndoe";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            throw UserExceptions.userNotFound(username);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User with user name 'johndoe' not found", exception.getReason());
    }
}
