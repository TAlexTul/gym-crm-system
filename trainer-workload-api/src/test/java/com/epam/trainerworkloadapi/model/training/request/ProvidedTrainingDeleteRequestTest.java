package com.epam.trainerworkloadapi.model.training.request;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProvidedTrainingDeleteRequestTest {

    @Test
    void testProvidedTrainingDeleteRequestCreation() {
        String trainerUsernames = "Jane.Jenkins";
        OffsetDateTime trainingDate = OffsetDateTime.now();
        long trainingDuration = 90L;

        ProvidedTrainingDeleteRequest request = new ProvidedTrainingDeleteRequest(
                trainerUsernames,
                trainingDate,
                trainingDuration
        );

        assertEquals(trainerUsernames, request.trainerUsernames());
        assertEquals(trainingDate, request.trainingDate());
        assertEquals(trainingDuration, request.trainingDuration());
    }

    @Test
    void testProvidedTrainingDeleteRequestEquality() {
        OffsetDateTime trainingDate = OffsetDateTime.now();
        ProvidedTrainingDeleteRequest request1 = new ProvidedTrainingDeleteRequest(
                "Jane.Jenkins", trainingDate, 90L
        );
        ProvidedTrainingDeleteRequest request2 = new ProvidedTrainingDeleteRequest(
                "Jane.Jenkins", trainingDate, 90L
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testProvidedTrainingDeleteRequestToString() {
        OffsetDateTime trainingDate = OffsetDateTime.now();
        ProvidedTrainingDeleteRequest request = new ProvidedTrainingDeleteRequest(
                "Jane.Jenkins", trainingDate, 90L
        );

        String toString = request.toString();

        assertTrue(toString.contains("Jane.Jenkins"));
        assertTrue(toString.contains(trainingDate.toString()));
        assertTrue(toString.contains("90"));
    }
}
