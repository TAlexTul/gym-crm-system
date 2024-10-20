package com.epam.gymcrmsystemapi.model.training.type.response;

import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingTypeResponseTest {

    @Test
    void testFromTrainingType() {
        TrainingType trainingType = new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING);

        TrainingTypeResponse response = TrainingTypeResponse.fromTrainingType(trainingType);

        assertEquals(trainingType.getId().ordinal(), response.id());
        assertEquals(trainingType.getType(), response.type());
    }
}
