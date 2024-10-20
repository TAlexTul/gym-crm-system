package com.epam.gymcrmsystemapi;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;

import java.time.OffsetDateTime;
import java.util.Set;

public final class Data {

    private Data() {
        throw new AssertionError("non-instantiable class");
    }

    public static final Long ID_USER_1 = 1L;
    public static final Long ID_USER_2 = 2L;
    public static final String FIRST_NAME_1 = "John";
    public static final String FIRST_NAME_2 = "Jane";
    public static final String LAST_NAME_1 = "Doe";
    public static final String LAST_NAME_2 = "Smith";
    public static final String USERNAME_1 = "John.Doe";
    public static final String USERNAME_2 = "Jane.Smith";
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
    public static final Specialization SPECIALIZATION_TYPE_1 =
            new Specialization(SpecializationType.BODYBUILDING_COACH, SpecializationType.BODYBUILDING_COACH);
    public static final Specialization SPECIALIZATION_TYPE_2 =
            new Specialization(SpecializationType.CARDIO_TRAINING_COACH, SpecializationType.CARDIO_TRAINING_COACH);
    public static final Long ID_TRAINING_1 = 1L;
    public static final Long ID_TRAINING_2 = 2L;
    public static final String TRAINING_NAME_1 = "Training 1";
    public static final String TRAINING_NAME_2 = "Training 2";
    public static final OffsetDateTime TRAINING_DATE_1 = OffsetDateTime.now();
    public static final OffsetDateTime TRAINING_DATE_2 = OffsetDateTime.now().plusMonths(1L);
    public static final Long TRAINING_DURATION_1 = 2L * 60 * 60 * 1000;
    public static final Long TRAINING_DURATION_2 = 3L * 60 * 60 * 1000;

    public static Set<Trainer> getTrainer1() {
        return Set.of(
                new Trainer(
                        ID_TRAINER_1,
                        SPECIALIZATION_TYPE_1,
                        getUser1(),
                        null,
                        null
                ));
    }

    public static Set<Trainer> getTrainer2() {
        return Set.of(
                new Trainer(
                        ID_TRAINER_2,
                        SPECIALIZATION_TYPE_2,
                        getUser2(),
                        null,
                        null
                ));
    }

    public static Set<Trainee> getTrainee1() {
        return Set.of(
                new Trainee(
                        ID_TRAINEE_1,
                        DATE_OF_BIRTH_1,
                        ADDRESS_1,
                        getUser1(),
                        null,
                        null
                ));
    }

    public static Set<Trainee> getTrainee2() {
        return Set.of(
                new Trainee(
                        ID_TRAINEE_2,
                        DATE_OF_BIRTH_2,
                        ADDRESS_2,
                        getUser2(),
                        null,
                        null
                ));
    }

    public static User getUser1() {
        return new User(
                ID_USER_1,
                FIRST_NAME_1,
                LAST_NAME_1,
                USERNAME_1,
                PASSWORD_1,
                UserStatus.ACTIVE
        );
    }

    public static User getUser2() {
        return new User(
                ID_USER_2,
                FIRST_NAME_2,
                LAST_NAME_2,
                USERNAME_2,
                PASSWORD_2,
                UserStatus.ACTIVE
        );
    }
}
