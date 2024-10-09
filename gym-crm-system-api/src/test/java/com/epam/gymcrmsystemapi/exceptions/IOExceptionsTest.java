package com.epam.gymcrmsystemapi.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IOExceptionsTest {

    @Test
    void testUncheckedIOException() {
        var ex = new IOException("File not found");

        ResponseStatusException rse = IOExceptions.IOException(ex);

        String message = rse.getMessage();
        String[] parts = message.split(" ", 3);
        int statusCode = Integer.parseInt(parts[0]);
        HttpStatus status = HttpStatus.valueOf(statusCode);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
        assertEquals(ex.getMessage(), rse.getReason());
    }

    @Test
    void testUncheckedIOExceptionWithDifferentMessage() {
        var ex = new IOException("Read error");

        ResponseStatusException rse = IOExceptions.IOException(ex);

        String message = rse.getMessage();
        String[] parts = message.split(" ", 3);
        int statusCode = Integer.parseInt(parts[0]);
        HttpStatus status = HttpStatus.valueOf(statusCode);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
        assertEquals(ex.getMessage(), rse.getReason());
    }
}
