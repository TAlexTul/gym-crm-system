package com.epam.trainerworkloadapi.model;

import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MonthlySummaryTrainingsResponseTest {

    @Test
    void testFromSummaryTrainingsDuration() {
        var id = UUID.randomUUID().toString();
        var username = "John.Doe";
        var firstName = "John";
        var lastName = "Doe";
        var status = UserStatus.ACTIVE;
        var trainings = List.of(
                new ProvidedTraining(Year.of(2024), Month.JANUARY, 60),
                new ProvidedTraining(Year.of(2024), Month.FEBRUARY, 120)
        );
        long summaryDuration = 180;

        var std = new SummaryTrainingsDuration(
                id, username, firstName, lastName, status, trainings, summaryDuration
        );

        var response = MonthlySummaryTrainingsResponse.fromSummaryTrainingsDuration(std);

        assertEquals(username, response.trainerUsername());
        assertEquals(firstName, response.trainerFirstName());
        assertEquals(lastName, response.trainerLastName());
        assertEquals(status, response.trainerStatus());
        assertEquals(trainings, response.trainings());
        assertEquals(summaryDuration, response.trainingSummaryDuration());
    }
}
