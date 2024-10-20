package com.epam.gymcrmsystemapi.model.training.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingResponseTest {

    @Test
    void testFromTraining() {
        var training = getTraining();

        var response = TrainingResponse.fromTraining(training);

        assertEquals(training.getId(), response.id());
        assertEquals(training.getTrainingName(), response.trainingName());

        assertEquals(training.getTrainingTypes().get(0).getId().ordinal(), response.trainingTypes().get(0).id());
        assertEquals(training.getTrainingTypes().get(0).getType(), response.trainingTypes().get(0).type());

        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());

        assertEquals(training.getTrainees().iterator().next().getId(),
                response.trainees().iterator().next().traineeId());
        assertEquals(training.getTrainees().iterator().next().getAddress(),
                response.trainees().iterator().next().address());
        assertEquals(training.getTrainees().iterator().next().getDateOfBirth(),
                response.trainees().iterator().next().dateOfBirth());

        assertEquals(training.getTrainers().iterator().next().getId(),
                response.trainers().iterator().next().trainerId());

        assertEquals(training.getTrainers().iterator().next().getSpecialization().getId().ordinal(),
                response.trainers().iterator().next().specialization().id());
        assertEquals(training.getTrainers().iterator().next().getSpecialization().getSpecialization(),
                response.trainers().iterator().next().specialization().specializationType());
    }

    @Test
    void testFromTrainingWithBasicAttributes() {
        var training = getTraining();
        training.setTrainees(null);
        training.setTrainers(null);
        training.setTrainingTypes(null);

        var response = TrainingResponse.fromTrainingWithBasicAttributes(training);

        assertEquals(training.getId(), response.id());
        assertNull(response.trainees());
        assertNull(response.trainers());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertNull(training.getTrainingTypes());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
    }

    private Training getTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainees(new HashSet<>(List.of(getTrainee())));
        training.setTrainers(new HashSet<>(List.of(getTrainer())));
        training.setTrainingName("Training 1");
        training.setTrainingTypes(new ArrayList<>(List.of(new TrainingType(Type.CARDIO_WORKOUT, Type.CARDIO_WORKOUT))));
        training.setTrainingDate(OffsetDateTime.now());
        training.setTrainingDuration(300000L);
        return training;
    }

    private Trainee getTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.parse("2007-12-03T09:15:30Z"));
        trainee.setAddress("123 Main St");
        trainee.setUser(getUser1());
        return trainee;
    }

    private Trainer getTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization(
                new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER));
        trainer.setUser(getUser2());
        return trainer;
    }

    private User getUser1() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Jenkins");
        user.setUsername("Jane.Jenkins");
        user.setPassword(null);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUser2() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword(null);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
