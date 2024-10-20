package com.epam.gymcrmsystemapi.model.training.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TrainingTypeTest {

    @Test
    void testConstructorAndGetters() {
        Type id = Type.STRENGTH_TRAINING;
        Type type = Type.STRENGTH_TRAINING;

        TrainingType trainingType = new TrainingType(id, type);

        assertEquals(id, trainingType.getId());
        assertEquals(type, trainingType.getType());
    }

    @Test
    void testSetters() {
        TrainingType trainingType = new TrainingType();

        Type id = Type.STRENGTH_TRAINING;
        Type type = Type.STRENGTH_TRAINING;

        trainingType.setId(id);
        trainingType.setType(type);

        assertEquals(id, trainingType.getId());
        assertEquals(type, trainingType.getType());
    }

    @Test
    void testEqualsAndHashCode() {
        TrainingType trainingType1 = new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING);
        TrainingType trainingType2 = new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING);
        TrainingType trainingType3 = new TrainingType(Type.CARDIO_WORKOUT, Type.CARDIO_WORKOUT);

        assertEquals(trainingType1, trainingType2);
        assertEquals(trainingType1.hashCode(), trainingType2.hashCode());

        assertNotEquals(trainingType1, trainingType3);
        assertNotEquals(trainingType1.hashCode(), trainingType3.hashCode());
    }
}
