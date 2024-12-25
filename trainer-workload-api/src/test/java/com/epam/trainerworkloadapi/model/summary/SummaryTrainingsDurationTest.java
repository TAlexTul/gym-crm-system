package com.epam.trainerworkloadapi.model.summary;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SummaryTrainingsDurationTest {

    @Test
    void testConstructorAndGetters() {
        var id = "1";
        var username = "john_doe";
        var firstName = "John";
        var lastName = "Doe";
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
        var id = "2";
        var username = "jane_doe";
        var firstName = "Jane";
        var lastName = "Doe";
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
        var summary1 = new SummaryTrainingsDuration(
                "1", "john_doe", "John", "Doe", UserStatus.ACTIVE,
                List.of(new ProvidedTraining(Year.of(2024), Month.JANUARY, 60)), 60L
        );
        var summary2 = new SummaryTrainingsDuration(
                "1", "john_doe", "John", "Doe", UserStatus.ACTIVE,
                List.of(new ProvidedTraining(Year.of(2024), Month.JANUARY, 60)), 60L
        );
        var summary3 = new SummaryTrainingsDuration(
                "2", "jane_doe", "Jane", "Doe", UserStatus.ACTIVE,
                List.of(new ProvidedTraining(Year.of(2023), Month.DECEMBER, 45)), 45L
        );

        assertEquals(summary1, summary2); // Objects are equal
        assertEquals(summary1.hashCode(), summary2.hashCode()); // Hash codes are equal
        assertNotEquals(summary1, summary3); // Objects are not equal
        assertNotEquals(summary1.hashCode(), summary3.hashCode()); // Hash codes are not equal
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
