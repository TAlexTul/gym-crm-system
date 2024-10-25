package com.epam.gymcrmsystemapi.model.training;

import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.epam.gymcrmsystemapi.Data.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TrainingTest {

    @Test
    public void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(ID_TRAINING_1, getTraining1().getId());
        assertEquals(getTrainee1(), getTraining1().getTrainees());
        assertEquals(getTrainer1(), getTraining1().getTrainers());
        assertEquals(TRAINING_NAME_1, getTraining1().getTrainingName());
        assertEquals(List.of(new TrainingType(Type.STRENGTH_TRAINING,
                Type.STRENGTH_TRAINING)), getTraining1().getTrainingTypes());
        assertEquals(TRAINING_DATE_1, getTraining1().getTrainingDate());
        assertEquals(TRAINING_DURATION_1, getTraining1().getTrainingDuration());
    }

    @Test
    void testEquals() {
        assertEquals(getTraining1(), getTraining1());
    }

    @Test
    void testNotEquals() {
        assertNotEquals(getTraining1(), getTraining2());
    }

    @Test
    void testHashCodeMatch() {
        assertEquals(getTraining1().hashCode(), getTraining1().hashCode());
    }

    @Test
    void testHashCodeNotMatch() {
        assertNotEquals(getTraining1().hashCode(), getTraining2().hashCode());
    }

    private Training getTraining1() {
        return new Training(
                ID_TRAINING_1,
                getTrainee1(),
                getTrainer1(),
                TRAINING_NAME_1,
                List.of(new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING)),
                TRAINING_DATE_1,
                TRAINING_DURATION_1
        );
    }

    private Training getTraining2() {
        return new Training(
                ID_TRAINING_2,
                getTrainee2(),
                getTrainer2(),
                TRAINING_NAME_2,
                List.of(new TrainingType(Type.CARDIO_WORKOUT, Type.CARDIO_WORKOUT)),
                TRAINING_DATE_2,
                TRAINING_DURATION_2
        );
    }
}
