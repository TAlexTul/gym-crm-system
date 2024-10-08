package com.epam.gymcrmsystemapi.model.trainee;

import com.epam.gymcrmsystemapi.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TraineeTest {

    @Test
    public void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(Data.ID_TRAINEE_1, getTrainee1().getId());
        assertEquals(Data.DATE_OF_BIRTH_1, getTrainee1().getDateOfBirth());
        assertEquals(Data.ADDRESS_1, getTrainee1().getAddress());
        assertEquals(Data.getUser1(), getTrainee1().getUser());
    }

    @Test
    public void testEquals() {
        assertEquals(getTrainee1(), getTrainee1());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(getTrainee1(), getTrainee2());
    }

    @Test
    public void testHashCodeMatch() {
        assertEquals(getTrainee1().hashCode(), getTrainee1().hashCode());
    }

    @Test
    public void testHashCodeNotMatch() {
        assertNotEquals(getTrainee1().hashCode(), getTrainee2().hashCode());
    }

    private Trainee getTrainee1() {
        var trainee = new Trainee();
        trainee.setId(Data.ID_TRAINEE_1);
        trainee.setDateOfBirth(Data.DATE_OF_BIRTH_1);
        trainee.setAddress(Data.ADDRESS_1);
        trainee.setUser(Data.getUser1());
        return trainee;
    }

    private Trainee getTrainee2() {
        var trainee = new Trainee();
        trainee.setId(Data.ID_TRAINEE_2);
        trainee.setDateOfBirth(Data.DATE_OF_BIRTH_2);
        trainee.setAddress(Data.ADDRESS_2);
        trainee.setUser(Data.getUser2());
        return trainee;
    }
}
