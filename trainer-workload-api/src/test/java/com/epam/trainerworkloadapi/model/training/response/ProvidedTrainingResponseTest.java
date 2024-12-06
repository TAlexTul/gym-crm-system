package com.epam.trainerworkloadapi.model.training.response;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.User;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ProvidedTrainingResponseTest {

    @Test
    void fromProvidedTraining_ShouldMapProvidedTrainingToResponse() {
        User user = new User(1L, "Jane", "Jenkins", "Jane.Jenkins", UserStatus.ACTIVE);
        ProvidedTraining training = new ProvidedTraining();
        training.setId(1L);
        training.setUser(user);
        training.setTrainingDate(OffsetDateTime.parse("2024-04-15T10:00:00Z"));
        training.setTrainingDuration(120L);

        ProvidedTrainingResponse response = ProvidedTrainingResponse.fromProvidedTraining(training);

        assertEquals(1L, response.id());
        assertEquals(2024, response.year());
        assertEquals(Month.APRIL, response.month());
        assertEquals(120L, response.trainingDuration());
    }

    @Test
    void fromProvidedTraining_ShouldThrowException_WhenTrainingDateIsNull() {
        ProvidedTraining training = new ProvidedTraining();
        training.setId(1L);
        training.setTrainingDuration(120L);

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> ProvidedTrainingResponse.fromProvidedTraining(training));
        assertEquals("Cannot invoke \"java.time.OffsetDateTime.getYear()\" because the return value of " +
                        "\"com.epam.trainerworkloadapi.model.training.ProvidedTraining.getTrainingDate()\" is null",
                exception.getMessage());
    }
}
