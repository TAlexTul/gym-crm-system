package com.epam.gymcrmsystemapi.model.trainer;

import com.epam.gymcrmsystemapi.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TrainerTest {

    @Test
    public void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(Data.ID_TRAINER_1, getTrainer1().getId());
        assertEquals(Data.SPECIALIZATION_1, getTrainer1().getSpecialization());
        assertEquals(Data.getUser1(), getTrainer1().getUser());
    }

    @Test
    public void testEquals() {
        assertEquals(getTrainer1(), getTrainer1());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(getTrainer1(), getTrainer2());
    }

    @Test
    public void testHashCodeMatch() {
        assertEquals(getTrainer1().hashCode(), getTrainer1().hashCode());
    }

    @Test
    public void testHashCodeNotMatch() {
        assertNotEquals(getTrainer1().hashCode(), getTrainer2().hashCode());
    }

    private Trainer getTrainer1() {
        var trainer = new Trainer();
        trainer.setId(Data.ID_TRAINER_1);
        trainer.setSpecialization(Data.SPECIALIZATION_1);
        trainer.setUser(Data.getUser1());
        return trainer;
    }

    private Trainer getTrainer2() {
        var trainer = new Trainer();
        trainer.setId(Data.ID_TRAINER_2);
        trainer.setSpecialization(Data.SPECIALIZATION_2);
        trainer.setUser(Data.getUser2());
        return trainer;
    }
}
