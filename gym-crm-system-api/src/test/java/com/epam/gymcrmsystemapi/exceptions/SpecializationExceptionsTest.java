package com.epam.gymcrmsystemapi.exceptions;

import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpecializationExceptionsTest {

    @Test
    void testSpecializationNotFound_Id() {
        SpecializationType id = SpecializationType.BODYBUILDING_COACH;

        ResponseStatusException rse = SpecializationExceptions.specializationNotFound(id);

        HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());

        assertEquals(HttpStatus.NOT_FOUND, status);
        assertEquals("Specialization with id '" + id + "' not found", rse.getReason());
    }
}
