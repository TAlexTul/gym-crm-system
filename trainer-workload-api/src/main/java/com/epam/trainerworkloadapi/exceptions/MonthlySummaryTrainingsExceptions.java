package com.epam.trainerworkloadapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class MonthlySummaryTrainingsExceptions {

    private MonthlySummaryTrainingsExceptions() {
    }

    public static ResponseStatusException monthlySummaryTrainingsDurationNotFound(String username) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Monthly summary trainings duration with user name '" + username + "' not found.");
    }
}
