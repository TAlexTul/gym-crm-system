package com.epam.gymcrmsystemapi.model.training.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingResponseTest {

    @Test
    void testFromTraining() {
        var traineeUser = new User();
        traineeUser.setId(1L);
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setUserName("John.Doe");
        traineeUser.setStatus(UserStatus.ACTIVE);
        var trainee = new Trainee();
        trainee.setUser(traineeUser);

        var trainerUser = new User();
        trainerUser.setId(1L);
        trainerUser.setFirstName("Alex");
        trainerUser.setLastName("Brown");
        trainerUser.setUserName("Alex.Brown");
        trainerUser.setStatus(UserStatus.ACTIVE);
        var trainer = new Trainer();
        trainer.setUser(trainerUser);

        var training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Boxing");
        training.setTrainingType(TrainingType.STRENGTH);
        training.setTrainingDate(OffsetDateTime.now());
        training.setTrainingDuration(Duration.ofHours(1));

        TrainingResponse response = TrainingResponse.fromTraining(training);

        assertEquals(training.getId(), response.id());
        assertEquals(traineeUser.getFirstName(), response.traineeFirstName());
        assertEquals(traineeUser.getLastName(), response.traineeLastName());
        assertEquals(traineeUser.getUserName(), response.traineeNickName());
        assertEquals(trainerUser.getFirstName(), response.trainerUserName());
        assertEquals(trainerUser.getLastName(), response.trainerLastName());
        assertEquals(trainerUser.getUserName(), response.trainerNickName());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertEquals(training.getTrainingType(), response.trainingType());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
    }

    @Test
    void testFromTrainingWithBasicAttributes() {
        var training = new Training();
        training.setId(1L);
        training.setTrainee(null);
        training.setTrainer(null);
        training.setTrainingName("Boxing");
        training.setTrainingType(TrainingType.STRENGTH);
        training.setTrainingDate(OffsetDateTime.now());
        training.setTrainingDuration(Duration.ofMinutes(90));

        var response = TrainingResponse.fromTrainingWithBasicAttributes(training);

        assertEquals(training.getId(), response.id());
        assertNull(response.traineeFirstName());
        assertNull(response.traineeLastName());
        assertNull(response.traineeNickName());
        assertNull(response.trainerUserName());
        assertNull(response.trainerLastName());
        assertNull(response.trainerNickName());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertEquals(training.getTrainingType(), response.trainingType());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
    }
}
