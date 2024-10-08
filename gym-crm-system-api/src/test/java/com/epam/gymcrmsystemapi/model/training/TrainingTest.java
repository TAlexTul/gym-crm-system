package com.epam.gymcrmsystemapi.model.training;

import com.epam.gymcrmsystemapi.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TrainingTest {

    @Test
    public void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(Data.ID_TRAINING_1, getTraining1().getId());
        assertEquals(Data.getTrainee1(), getTraining1().getTrainee());
        assertEquals(Data.getTrainer1(), getTraining1().getTrainer());
        assertEquals(Data.TRAINING_NAME_1, getTraining1().getTrainingName());
        assertEquals(TrainingType.STRENGTH, getTraining1().getTrainingType());
        assertEquals(Data.TRAINING_DATE_1, getTraining1().getTrainingDate());
        assertEquals(Data.TRAINING_DURATION_1, getTraining1().getTrainingDuration());
    }

    @Test
    public void testEquals() {
        assertEquals(getTraining1(), getTraining1());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(getTraining1(), getTraining2());
    }

    @Test
    public void testHashCodeMatch() {
        assertEquals(getTraining1().hashCode(), getTraining1().hashCode());
    }

    @Test
    public void testHashCodeNotMatch() {
        assertNotEquals(getTraining1().hashCode(), getTraining2().hashCode());
    }

    private Training getTraining1() {
        var training = new Training();
        training.setId(Data.ID_TRAINING_1);
        training.setTrainee(Data.getTrainee1());
        training.setTrainer(Data.getTrainer1());
        training.setTrainingName(Data.TRAINING_NAME_1);
        training.setTrainingType(TrainingType.STRENGTH);
        training.setTrainingDate(Data.TRAINING_DATE_1);
        training.setTrainingDuration(Data.TRAINING_DURATION_1);
        return training;
    }

    private Training getTraining2() {
        var training = new Training();
        training.setId(Data.ID_TRAINING_2);
        training.setTrainee(Data.getTrainee2());
        training.setTrainer(Data.getTrainer2());
        training.setTrainingName(Data.TRAINING_NAME_2);
        training.setTrainingType(TrainingType.CARDIO);
        training.setTrainingDate(Data.TRAINING_DATE_2);
        training.setTrainingDuration(Data.TRAINING_DURATION_2);
        return training;
    }
}
