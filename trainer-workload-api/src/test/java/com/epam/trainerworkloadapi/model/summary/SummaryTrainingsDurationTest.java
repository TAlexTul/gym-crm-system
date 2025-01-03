package com.epam.trainerworkloadapi.model.summary;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SummaryTrainingsDurationTest {

    @Test
    void testConstructorAndGetters() {
        var id = UUID.randomUUID().toString();
        var username = "Jane.Jenkins";
        var firstName = "Jane";
        var lastName = "Jenkins";
        var status = UserStatus.ACTIVE;
        var trainings = List.of(
                new ProvidedTraining(Year.of(2024), Month.JANUARY, 60)
        );
        Long summaryDuration = 60L;

        var summary = new SummaryTrainingsDuration(
                id, username, firstName, lastName, status, trainings, summaryDuration
        );

        assertEquals(id, summary.getId());
        assertEquals(username, summary.getUsername());
        assertEquals(firstName, summary.getFirstName());
        assertEquals(lastName, summary.getLastName());
        assertEquals(status, summary.getUserStatus());
        assertEquals(trainings, summary.getTrainings());
        assertEquals(summaryDuration, summary.getSummaryTrainingsDuration());
    }

    @Test
    void testSetters() {
        var summary = new SummaryTrainingsDuration();
        var id = UUID.randomUUID().toString();
        var username = "Jane.Jenkins";
        var firstName = "Jane";
        var lastName = "Jenkins";
        var status = UserStatus.ACTIVE;
        var trainings = List.of(
                new ProvidedTraining(Year.of(2023), Month.DECEMBER, 45)
        );
        Long summaryDuration = 45L;

        summary.setId(id);
        summary.setUsername(username);
        summary.setFirstName(firstName);
        summary.setLastName(lastName);
        summary.setUserStatus(status);
        summary.setTrainings(trainings);
        summary.setSummaryTrainingsDuration(summaryDuration);

        assertEquals(id, summary.getId());
        assertEquals(username, summary.getUsername());
        assertEquals(firstName, summary.getFirstName());
        assertEquals(lastName, summary.getLastName());
        assertEquals(status, summary.getUserStatus());
        assertEquals(trainings, summary.getTrainings());
        assertEquals(summaryDuration, summary.getSummaryTrainingsDuration());
    }

    @Test
    void testEqualsAndHashCode() {
        var id = UUID.randomUUID().toString();
        var anotherId = UUID.randomUUID().toString();
        var summary1 = new SummaryTrainingsDuration(
                id, "Jane.Jenkins", "Jane", "Jenkins", UserStatus.ACTIVE,
                List.of(new ProvidedTraining(Year.of(2024), Month.JANUARY, 60)), 60L
        );
        var summary2 = new SummaryTrainingsDuration(
                id, "Jane.Jenkins", "Jane", "Jenkins", UserStatus.ACTIVE,
                List.of(new ProvidedTraining(Year.of(2024), Month.JANUARY, 60)), 60L
        );
        var summary3 = new SummaryTrainingsDuration(
                anotherId, "Jane.Doe", "Jane", "Doe", UserStatus.ACTIVE,
                List.of(new ProvidedTraining(Year.of(2023), Month.DECEMBER, 45)), 45L
        );

        assertEquals(summary1, summary2);
        assertEquals(summary1.hashCode(), summary2.hashCode());
        assertNotEquals(summary1, summary3);
        assertNotEquals(summary1.hashCode(), summary3.hashCode());
    }

    @Test
    void testDefaultConstructor() {
        var summary = new SummaryTrainingsDuration();

        assertNull(summary.getId());
        assertNull(summary.getUsername());
        assertNull(summary.getFirstName());
        assertNull(summary.getLastName());
        assertNull(summary.getUserStatus());
        assertNotNull(summary.getTrainings());
        assertTrue(summary.getTrainings().isEmpty());
        assertNull(summary.getSummaryTrainingsDuration());
    }
}
