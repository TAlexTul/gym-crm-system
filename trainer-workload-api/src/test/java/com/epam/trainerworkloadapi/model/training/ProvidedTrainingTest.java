package com.epam.trainerworkloadapi.model.training;

import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class ProvidedTrainingTest {

    @Test
    void testProvidedTrainingConstructorAndGetters() {
        Year year = Year.of(2024);
        Month month = Month.JANUARY;
        long duration = 60;

        ProvidedTraining training = new ProvidedTraining(year, month, duration);

        assertEquals(year, training.getYear());
        assertEquals(month, training.getMonth());
        assertEquals(duration, training.getTrainingDuration());
    }

    @Test
    void testSetters() {
        ProvidedTraining training = new ProvidedTraining();
        Year year = Year.of(2024);
        Month month = Month.FEBRUARY;
        long duration = 90;

        training.setYear(year);
        training.setMonth(month);
        training.setTrainingDuration(duration);

        assertEquals(year, training.getYear());
        assertEquals(month, training.getMonth());
        assertEquals(duration, training.getTrainingDuration());
    }

    @Test
    void testEqualsAndHashCode() {
        ProvidedTraining training1 = new ProvidedTraining(Year.of(2024), Month.MARCH, 120);
        ProvidedTraining training2 = new ProvidedTraining(Year.of(2024), Month.MARCH, 120);
        ProvidedTraining training3 = new ProvidedTraining(Year.of(2025), Month.APRIL, 150);

        assertEquals(training1, training2);
        assertEquals(training1.hashCode(), training2.hashCode());
        assertNotEquals(training1, training3);
        assertNotEquals(training1.hashCode(), training3.hashCode());
    }
}
