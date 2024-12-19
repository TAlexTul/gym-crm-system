package com.epam.trainerworkloadapi.model.training.request;

import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProvidedTrainingSaveRequestTest {

    @Test
    void testProvidedTrainingSaveRequestCreation() {
        String trainerUsername = "Jane.Jenkins";
        String trainerFirstName = "Jane";
        String trainerLastName = "Jenkins";
        UserStatus trainerStatus = UserStatus.ACTIVE;
        OffsetDateTime trainingDate = OffsetDateTime.now();
        long trainingDuration = 90L;

        ProvidedTrainingSaveRequest request = new ProvidedTrainingSaveRequest(
                trainerUsername,
                trainerFirstName,
                trainerLastName,
                trainerStatus,
                trainingDate,
                trainingDuration
        );

        assertEquals(trainerUsername, request.trainerUsername());
        assertEquals(trainerFirstName, request.trainerFirstName());
        assertEquals(trainerLastName, request.trainerLastName());
        assertEquals(trainerStatus, request.trainerStatus());
        assertEquals(trainingDate, request.trainingDate());
        assertEquals(trainingDuration, request.trainingDuration());
    }

    @Test
    void testProvidedTrainingSaveRequestEquality() {
        OffsetDateTime trainingDate = OffsetDateTime.now();
        ProvidedTrainingSaveRequest request1 = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins", "Jane", "Jenkins", UserStatus.ACTIVE, trainingDate, 90L
        );
        ProvidedTrainingSaveRequest request2 = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins", "Jane", "Jenkins", UserStatus.ACTIVE, trainingDate, 90L
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testProvidedTrainingSaveRequestToString() {
        OffsetDateTime trainingDate = OffsetDateTime.now();
        ProvidedTrainingSaveRequest request = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins", "Jane", "Jenkins", UserStatus.ACTIVE, trainingDate, 90L
        );

        String toString = request.toString();

        assertTrue(toString.contains("Jane.Jenkins"));
        assertTrue(toString.contains("Jane"));
        assertTrue(toString.contains("Jenkins"));
        assertTrue(toString.contains("ACTIVE"));
        assertTrue(toString.contains(trainingDate.toString()));
        assertTrue(toString.contains("90"));
    }
}
