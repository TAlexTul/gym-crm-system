package com.epam.gymcrmsystemapi.model.trainer;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {

    @Test
    void testTrainerConstructor() {
        Specialization specialization = new Specialization();
        User user = new User();
        Set<Training> trainings = new HashSet<>();
        Set<Trainee> trainees = new HashSet<>();

        Trainer trainer = new Trainer(1L, specialization, user, trainings, trainees);

        assertNotNull(trainer);
        assertEquals(1L, trainer.getId());
        assertEquals(specialization, trainer.getSpecialization());
        assertEquals(user, trainer.getUser());
        assertEquals(trainings, trainer.getTrainings());
        assertEquals(trainees, trainer.getTrainees());
    }

    @Test
    void testSetAndGetTrainings() {
        Trainer trainer = new Trainer();
        Set<Training> trainings = new HashSet<>();
        Training training = new Training();
        trainings.add(training);

        trainer.setTrainings(trainings);

        assertNotNull(trainer.getTrainings());
        assertTrue(trainer.getTrainings().contains(training));
    }

    @Test
    void testSetAndGetTrainees() {
        Trainer trainer = new Trainer();
        Set<Trainee> trainees = new HashSet<>();
        Trainee trainee = new Trainee();
        trainees.add(trainee);

        trainer.setTrainees(trainees);

        assertNotNull(trainer.getTrainees());
        assertTrue(trainer.getTrainees().contains(trainee));
    }

    @Test
    void testEqualsAndHashCode() {
        Trainer trainer1 = new Trainer(1L, null, null, new HashSet<>(), new HashSet<>());
        Trainer trainer2 = new Trainer(1L, null, null, new HashSet<>(), new HashSet<>());
        Trainer trainer3 = new Trainer(2L, null, null, new HashSet<>(), new HashSet<>());

        assertEquals(trainer1, trainer2);
        assertNotEquals(trainer1, trainer3);
        assertEquals(trainer1.hashCode(), trainer2.hashCode());
    }
}
