package com.epam.gymcrmsystemapi.model.trainee;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.user.User;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    @Test
    void testTraineeConstructor() {
        String address = "123 Test St.";
        OffsetDateTime dateOfBirth = OffsetDateTime.now();
        User user = new User();
        Set<Training> trainings = new HashSet<>();
        Set<Trainer> trainers = new HashSet<>();

        Trainee trainee = new Trainee(1L, dateOfBirth, address, user, trainings, trainers);

        assertNotNull(trainee);
        assertEquals(address, trainee.getAddress());
        assertEquals(dateOfBirth, trainee.getDateOfBirth());
        assertEquals(user, trainee.getUser());
        assertEquals(trainings, trainee.getTrainings());
        assertEquals(trainers, trainee.getTrainers());
    }

    @Test
    void testSetAndGetAddress() {
        Trainee trainee = new Trainee();
        String address = "456 Test Avenue";
        trainee.setAddress(address);

        assertEquals(address, trainee.getAddress());
    }

    @Test
    void testSetAndGetDateOfBirth() {
        Trainee trainee = new Trainee();
        OffsetDateTime dateOfBirth = OffsetDateTime.now();
        trainee.setDateOfBirth(dateOfBirth);

        assertEquals(dateOfBirth, trainee.getDateOfBirth());
    }

    @Test
    void testSetAndGetUser() {
        Trainee trainee = new Trainee();
        User user = new User();
        trainee.setUser(user);

        assertEquals(user, trainee.getUser());
    }

    @Test
    void testSetAndGetTrainings() {
        Trainee trainee = new Trainee();
        Set<Training> trainings = new HashSet<>();
        trainee.setTrainings(trainings);

        assertEquals(trainings, trainee.getTrainings());
    }

    @Test
    void testSetAndGetTrainers() {
        Trainee trainee = new Trainee();
        Set<Trainer> trainers = new HashSet<>();
        trainee.setTrainers(trainers);

        assertEquals(trainers, trainee.getTrainers());
    }

    @Test
    void testEqualsAndHashCode() {
        Trainee trainee1 = new Trainee(
                1L, OffsetDateTime.now(), "Address 1", new User(), new HashSet<>(), new HashSet<>());
        Trainee trainee2 = new Trainee(
                1L, OffsetDateTime.now(), "Address 1", new User(), new HashSet<>(), new HashSet<>());
        Trainee trainee3 = new Trainee(
                2L, OffsetDateTime.now(), "Address 2", new User(), new HashSet<>(), new HashSet<>());

        assertEquals(trainee1, trainee2);
        assertNotEquals(trainee1, trainee3);
        assertEquals(trainee1.hashCode(), trainee2.hashCode());
        assertNotEquals(trainee1.hashCode(), trainee3.hashCode());
    }

    @Test
    void testDefaultConstructor() {
        Trainee trainee = new Trainee();

        assertNull(trainee.getDateOfBirth());
        assertNull(trainee.getAddress());
        assertNull(trainee.getUser());
        assertTrue(trainee.getTrainings().isEmpty());
        assertTrue(trainee.getTrainers().isEmpty());
    }
}
