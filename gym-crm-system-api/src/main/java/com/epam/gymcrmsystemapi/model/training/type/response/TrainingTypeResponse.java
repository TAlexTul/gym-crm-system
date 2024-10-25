package com.epam.gymcrmsystemapi.model.training.type.response;

import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;

public record TrainingTypeResponse(Integer id,
                                   Type type) {

    public static TrainingTypeResponse fromTrainingType(TrainingType trainingType) {
        return new TrainingTypeResponse(
                trainingType.getId().ordinal(),
                trainingType.getType()
        );
    }
}
