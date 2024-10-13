package com.epam.gymcrmsystemapi.model.training.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.training.Type;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingResponseTest {

    @Test
    void testFromTraining() {
        var traineeUser = new User();
        traineeUser.setId(1L);
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setUsername("John.Doe");
        traineeUser.setStatus(UserStatus.ACTIVE);
        var trainee = new Trainee();
        trainee.setUser(traineeUser);

        var trainerUser = new User();
        trainerUser.setId(1L);
        trainerUser.setFirstName("Alex");
        trainerUser.setLastName("Brown");
        trainerUser.setUsername("Alex.Brown");
        trainerUser.setStatus(UserStatus.ACTIVE);
        var trainer = new Trainer();
        trainer.setUser(trainerUser);

        var training = new Training();
        training.setId(1L);
        training.setTrainees(Set.of(trainee));
        training.setTrainers(Set.of(trainer));
        training.setTrainingName("Training 1");
        training.setTrainingTypes(List.of(new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING)));
        training.setTrainingDate(OffsetDateTime.now());
        training.setTrainingDuration(300000L);

        var response = TrainingResponse.fromTraining(training);

        assertEquals(training.getId(), response.id());
        assertEquals(traineeUser.getFirstName(), response.trainees().iterator().next().getUser().getFirstName());
        assertEquals(traineeUser.getLastName(), response.trainees().iterator().next().getUser().getLastName());
        assertEquals(traineeUser.getUsername(), response.trainees().iterator().next().getUser().getUsername());
        assertEquals(trainerUser.getFirstName(), response.trainers().iterator().next().getUser().getFirstName());
        assertEquals(trainerUser.getLastName(), response.trainers().iterator().next().getUser().getLastName());
        assertEquals(trainerUser.getUsername(), response.trainers().iterator().next().getUser().getUsername());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertEquals(training.getTrainingTypes(), response.trainingTypes());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
    }

    @Test
    void testFromTrainingWithBasicAttributes() {
        var training = new Training();
        training.setId(1L);
        training.setTrainees(null);
        training.setTrainers(null);
        training.setTrainingName("Training 1");
        training.setTrainingTypes(List.of(new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING)));
        training.setTrainingDate(OffsetDateTime.now());
        training.setTrainingDuration(300000L);

        var response = TrainingResponse.fromTrainingWithBasicAttributes(training);

        assertEquals(training.getId(), response.id());
        assertNull(response.trainees());
        assertNull(response.trainers());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertEquals(training.getTrainingTypes(), response.trainingTypes());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
    }
}
