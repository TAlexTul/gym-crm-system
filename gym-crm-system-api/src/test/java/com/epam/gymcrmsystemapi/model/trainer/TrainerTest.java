package com.epam.gymcrmsystemapi.model.trainer;

import org.junit.jupiter.api.Test;

import static com.epam.gymcrmsystemapi.Data.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TrainerTest {

    @Test
    void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(ID_TRAINER_1, getTrainer1().getId());
        assertEquals(SPECIALIZATION_1, getTrainer1().getSpecialization());
        assertEquals(getUser1(), getTrainer1().getUser());
    }

    @Test
    void testEquals() {
        assertEquals(getTrainer1(), getTrainer1());
    }

    @Test
    void testNotEquals() {
        assertNotEquals(getTrainer1(), getTrainer2());
    }

    @Test
    void testHashCodeMatch() {
        assertEquals(getTrainer1().hashCode(), getTrainer1().hashCode());
    }

    @Test
    void testHashCodeNotMatch() {
        assertNotEquals(getTrainer1().hashCode(), getTrainer2().hashCode());
    }

    private Trainer getTrainer1() {
        return new Trainer(
                ID_TRAINER_1,
                SPECIALIZATION_1,
                getUser1()
        );
    }

    private Trainer getTrainer2() {
        return new Trainer(
                ID_TRAINER_2,
                SPECIALIZATION_2,
                getUser2()
        );
    }
}
