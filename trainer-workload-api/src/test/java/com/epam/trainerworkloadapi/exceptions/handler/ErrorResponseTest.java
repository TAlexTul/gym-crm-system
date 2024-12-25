package com.epam.trainerworkloadapi.exceptions.handler;

import com.epam.trainerworkloadapi.exceptions.handler.ErrorResponse;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testErrorResponseCreation() {
        Date timestamp = new Date();
        int status = 404;
        String error = "Not Found";
        String path = "/trainers/1";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, path);

        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(status, errorResponse.status());
        assertEquals(error, errorResponse.error());
        assertEquals(path, errorResponse.path());
    }

    @Test
    void testErrorResponseDefaultValues() {
        Date timestamp = new Date();
        ErrorResponse errorResponse = new ErrorResponse(timestamp, 500, "Internal Server Error", "/");

        assertEquals(500, errorResponse.status());
        assertEquals("Internal Server Error", errorResponse.error());
        assertEquals("/", errorResponse.path());
        assertNotNull(errorResponse.timestamp());
    }
}