package com.epam.trainerworkloadapi.model.training;

import com.epam.trainerworkloadapi.model.user.User;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProvidedTrainingTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        ProvidedTraining training = new ProvidedTraining();
        User user = new User();
        OffsetDateTime now = OffsetDateTime.now();
        long duration = 60L;

        training.setId(1L);
        training.setUser(user);
        training.setTrainingDate(now);
        training.setTrainingDuration(duration);

        assertEquals(1L, training.getId());
        assertEquals(user, training.getUser());
        assertEquals(now, training.getTrainingDate());
        assertEquals(duration, training.getTrainingDuration());
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        OffsetDateTime now = OffsetDateTime.now();

        ProvidedTraining training1 = new ProvidedTraining();
        training1.setId(1L);
        training1.setTrainingDate(now);
        training1.setTrainingDuration(60L);

        ProvidedTraining training2 = new ProvidedTraining();
        training2.setId(1L);
        training2.setTrainingDate(now);
        training2.setTrainingDuration(60L);

        ProvidedTraining training3 = new ProvidedTraining();
        training3.setId(2L);
        training3.setTrainingDate(now);
        training3.setTrainingDuration(30L);

        assertEquals(training1, training2);
        assertNotEquals(training1, training3);

        assertEquals(training1.hashCode(), training2.hashCode());
        assertNotEquals(training1.hashCode(), training3.hashCode());
    }

    @Test
    void shouldHaveDefaultConstructor() {
        ProvidedTraining training = new ProvidedTraining();
        assertNotNull(training);
    }
}
