package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
public class TrainingMapper implements DataMapper<Training> {

    @Override
    public Training map(String[] data) {
        Long id = Long.parseLong(data[0]);
        String trainingName = data[1];
        TrainingType trainingType = TrainingType.valueOf(data[2]);
        OffsetDateTime trainingDate = OffsetDateTime.parse(data[3]);
        Duration trainingDuration = Duration.parse(data[4]);

        var training = new Training();
        training.setId(id);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);

        return training;
    }
}
