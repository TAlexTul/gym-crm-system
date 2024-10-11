package com.epam.gymcrmsystemapi;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;

import java.time.Duration;
import java.time.OffsetDateTime;

public class Data {

    public static final Long ID_USER_1 = 1L;
    public static final Long ID_USER_2 = 2L;
    public static final String FIRST_NAME_1 = "John";
    public static final String FIRST_NAME_2 = "Jane";
    public static final String LAST_NAME_1 = "Doe";
    public static final String LAST_NAME_2 = "Smith";
    public static final String USER_NAME_1 = "John.Doe";
    public static final String USER_NAME_2 = "Jane.Smith";
    public static final String PASSWORD_1 = "aB9dE4fGhJ";
    public static final String PASSWORD_2 = "aB9dE4fGhJ";
    public static final Long ID_TRAINEE_1 = 1L;
    public static final Long ID_TRAINEE_2 = 2L;
    public static final OffsetDateTime DATE_OF_BIRTH_1 = OffsetDateTime.now();
    public static final OffsetDateTime DATE_OF_BIRTH_2 = OffsetDateTime.now().plusDays(1L);
    public static final String ADDRESS_1 = "123 Main St";
    public static final String ADDRESS_2 = "101 Independence Avenue";
    public static final Long ID_TRAINER_1 = 1L;
    public static final Long ID_TRAINER_2 = 2L;
    public static final String SPECIALIZATION_1 = "Box";
    public static final String SPECIALIZATION_2 = "Football";
    public static final Long ID_TRAINING_1 = 1L;
    public static final Long ID_TRAINING_2 = 2L;
    public static final String TRAINING_NAME_1 = "Training 1";
    public static final String TRAINING_NAME_2 = "Training 2";
    public static final OffsetDateTime TRAINING_DATE_1 = OffsetDateTime.now();
    public static final OffsetDateTime TRAINING_DATE_2 = OffsetDateTime.now().plusMonths(1L);
    public static final Duration TRAINING_DURATION_1 = Duration.parse("PT1H30M");
    public static final Duration TRAINING_DURATION_2 = Duration.parse("PT2H30M");

    private Training getTraining1() {
        var training = new Training();
        training.setId(ID_TRAINING_1);
        training.setTrainee(getTrainee1());
        training.setTrainer(getTrainer1());
        training.setTrainingName(TRAINING_NAME_1);
        training.setTrainingType(TrainingType.STRENGTH);
        training.setTrainingDate(TRAINING_DATE_1);
        training.setTrainingDuration(TRAINING_DURATION_1);
        return training;
    }

    private Training getTraining2() {
        var training = new Training();
        training.setId(ID_TRAINING_2);
        training.setTrainee(getTrainee2());
        training.setTrainer(getTrainer2());
        training.setTrainingName(TRAINING_NAME_2);
        training.setTrainingType(TrainingType.CARDIO);
        training.setTrainingDate(TRAINING_DATE_2);
        training.setTrainingDuration(Data.TRAINING_DURATION_2);
        return training;
    }

    public static Trainer getTrainer1() {
        var trainer = new Trainer();
        trainer.setId(ID_TRAINER_1);
        trainer.setSpecialization(SPECIALIZATION_1);
        trainer.setUser(Data.getUser1());
        return trainer;
    }

    public static Trainer getTrainer2() {
        var trainer = new Trainer();
        trainer.setId(ID_TRAINER_2);
        trainer.setSpecialization(SPECIALIZATION_2);
        trainer.setUser(Data.getUser2());
        return trainer;
    }

    public static Trainee getTrainee1() {
        var trainee = new Trainee();
        trainee.setId(ID_TRAINEE_1);
        trainee.setDateOfBirth(DATE_OF_BIRTH_1);
        trainee.setAddress(ADDRESS_1);
        trainee.setUser(getUser1());
        return trainee;
    }

    public static Trainee getTrainee2() {
        var trainee = new Trainee();
        trainee.setId(ID_TRAINEE_2);
        trainee.setDateOfBirth(DATE_OF_BIRTH_2);
        trainee.setAddress(ADDRESS_2);
        trainee.setUser(getUser2());
        return trainee;
    }

    public static User getUser1() {
        var user = new User();
        user.setId(ID_USER_1);
        user.setFirstName(FIRST_NAME_1);
        user.setLastName(LAST_NAME_1);
        user.setUserName(USER_NAME_1);
        user.setPassword(PASSWORD_1);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    public static User getUser2() {
        var user = new User();
        user.setId(ID_USER_2);
        user.setFirstName(FIRST_NAME_2);
        user.setLastName(LAST_NAME_2);
        user.setUserName(USER_NAME_2);
        user.setPassword(PASSWORD_2);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
