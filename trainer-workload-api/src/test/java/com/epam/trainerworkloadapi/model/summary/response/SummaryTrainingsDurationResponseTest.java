package com.epam.trainerworkloadapi.model.summary.response;

import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SummaryTrainingsDurationResponseTest {

    @Test
    void testFormSummaryTrainingsDuration() {
        var summary = new SummaryTrainingsDuration(
                UUID.randomUUID().toString(),
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                List.of(new ProvidedTraining(Year.of(2024), Month.JANUARY, 60)),
                60L
        );

        var response =
                SummaryTrainingsDurationResponse.formSummaryTrainingsDuration(summary);

        assertNotNull(response);
        assertEquals(summary.getId(), response.id());
        assertEquals(summary.getUsername(), response.trainerUsername());
        assertEquals(summary.getFirstName(), response.trainerFirstName());
        assertEquals(summary.getLastName(), response.trainerLastName());
        assertEquals(summary.getUserStatus(), response.trainerStatus());
        assertEquals(summary.getTrainings(), response.providedTrainings());
        assertEquals(summary.getSummaryTrainingsDuration(), response.summaryTrainingsDuration());
    }

    @Test
    void testResponseFields() {
        var id = UUID.randomUUID().toString();
        var username = "Jane.Jenkins";
        var firstName = "Jane";
        var lastName = "Jenkins";
        var status = UserStatus.ACTIVE;
        var trainings = List.of(
                new ProvidedTraining(Year.of(2023), Month.DECEMBER, 45)
        );
        Long summaryDuration = 45L;

        var response = new SummaryTrainingsDurationResponse(
                id,
                username,
                firstName,
                lastName,
                status,
                trainings,
                summaryDuration
        );

        assertEquals(id, response.id());
        assertEquals(username, response.trainerUsername());
        assertEquals(firstName, response.trainerFirstName());
        assertEquals(lastName, response.trainerLastName());
        assertEquals(status, response.trainerStatus());
        assertEquals(trainings, response.providedTrainings());
        assertEquals(summaryDuration, response.summaryTrainingsDuration());
    }

    @Test
    void testEmptyTrainingsList() {
        var summary = new SummaryTrainingsDuration(
                UUID.randomUUID().toString(),
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                List.of(),
                0L
        );

        var response =
                SummaryTrainingsDurationResponse.formSummaryTrainingsDuration(summary);

        assertNotNull(response);
        assertEquals(0, response.providedTrainings().size());
        assertEquals(0L, response.summaryTrainingsDuration());
    }
}
