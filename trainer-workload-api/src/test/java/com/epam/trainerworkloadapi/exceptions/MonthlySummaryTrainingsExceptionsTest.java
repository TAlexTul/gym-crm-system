package com.epam.trainerworkloadapi.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class MonthlySummaryTrainingsExceptionsTest {

    @Test
    void testMonthlySummaryTrainingsDurationNotFound() {
        var username = "testUser";

        ResponseStatusException exception =
                MonthlySummaryTrainingsExceptions.monthlySummaryTrainingsDurationNotFound(username);

        assertNotNull(exception, "The exception should not be null");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "The HTTP status should be NOT_FOUND");
        assertEquals(
                "Monthly summary trainings duration with user name 'testUser' not found.",
                exception.getReason(),
                "The exception reason message should match the expected value"
        );
    }
}
