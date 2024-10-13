package com.epam.gymcrmsystemapi.model.trainee;

import org.junit.jupiter.api.Test;

import static com.epam.gymcrmsystemapi.Data.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TraineeTest {

    @Test
    void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(ID_TRAINEE_1, getTrainee1().getId());
        assertEquals(DATE_OF_BIRTH_1, getTrainee1().getDateOfBirth());
        assertEquals(ADDRESS_1, getTrainee1().getAddress());
        assertEquals(getUser1(), getTrainee1().getUser());
    }

    @Test
    void testEquals() {
        assertEquals(getTrainee1(), getTrainee1());
    }

    @Test
    void testNotEquals() {
        assertNotEquals(getTrainee1(), getTrainee2());
    }

    @Test
    void testHashCodeMatch() {
        assertEquals(getTrainee1().hashCode(), getTrainee1().hashCode());
    }

    @Test
    void testHashCodeNotMatch() {
        assertNotEquals(getTrainee1().hashCode(), getTrainee2().hashCode());
    }

    private Trainee getTrainee1() {
        return new Trainee(
                ID_TRAINEE_1,
                DATE_OF_BIRTH_1,
                ADDRESS_1,
                getUser1()
        );
    }

    private Trainee getTrainee2() {
        return new Trainee(
                ID_TRAINEE_2,
                DATE_OF_BIRTH_2,
                ADDRESS_2,
                getUser2()
        );
    }
}
