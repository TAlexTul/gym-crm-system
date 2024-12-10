package com.epam.trainerworkloadapi.model;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.model.user.User;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonthlySummaryTrainingsResponseTest {

    @Test
    void fromEmptyListProvidedTraining_ShouldCreateResponseWithNullValues() {
        MonthlySummaryTrainingsRequest request = new MonthlySummaryTrainingsRequest(
                "johndoe", "John", "Doe", UserStatus.ACTIVE, null
        );

        MonthlySummaryTrainingsResponse response = MonthlySummaryTrainingsResponse.fromEmptyListProvidedTraining(request);

        assertEquals("johndoe", response.trainerUsername());
        assertEquals("John", response.trainerFirstName());
        assertEquals("Doe", response.trainerLastName());
        assertEquals(UserStatus.ACTIVE, response.trainerStatus());
        assertNull(response.trainings());
        assertNull(response.trainingSummaryDuration());
    }

    @Test
    void fromListProvidedTraining_ShouldCreateResponseWithTrainingsAndSummaryDuration() {
        User user = new User(1L, "John", "Doe", "johndoe", UserStatus.ACTIVE);

        ProvidedTraining training1 = new ProvidedTraining();
        training1.setId(1L);
        training1.setUser(user);
        training1.setTrainingDate(OffsetDateTime.parse("2024-04-15T10:00:00Z"));
        training1.setTrainingDuration(60L);

        ProvidedTraining training2 = new ProvidedTraining();
        training2.setId(2L);
        training2.setUser(user);
        training2.setTrainingDate(OffsetDateTime.parse("2024-04-16T10:00:00Z"));
        training2.setTrainingDuration(90L);

        List<ProvidedTraining> trainings = List.of(training1, training2);

        MonthlySummaryTrainingsResponse response = MonthlySummaryTrainingsResponse.fromListProvidedTraining(trainings);

        assertEquals("johndoe", response.trainerUsername());
        assertEquals("John", response.trainerFirstName());
        assertEquals("Doe", response.trainerLastName());
        assertEquals(UserStatus.ACTIVE, response.trainerStatus());

        assertNotNull(response.trainings());
        assertEquals(2, response.trainings().size());
        assertEquals(150L, response.trainingSummaryDuration());

        ProvidedTrainingResponse firstResponse = response.trainings().get(0);
        assertEquals(1L, firstResponse.id());
        assertEquals(2024, firstResponse.year());
        assertEquals(java.time.Month.APRIL, firstResponse.month());
        assertEquals(60L, firstResponse.trainingDuration());
    }

    @Test
    void fromListProvidedTraining_ShouldHandleEmptyList() {
        List<ProvidedTraining> trainings = List.of();

        assertThrows(IndexOutOfBoundsException.class,
                () -> MonthlySummaryTrainingsResponse.fromListProvidedTraining(trainings));
    }
}
