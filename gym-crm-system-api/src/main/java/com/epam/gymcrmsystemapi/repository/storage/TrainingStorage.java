package com.epam.gymcrmsystemapi.repository.storage;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.repository.storage.mapper.DataMapper;
import com.epam.gymcrmsystemapi.repository.storage.reader.DataReader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

@Component
public class TrainingStorage {

    @Value("${storage.file.path.training}")
    private String filePath;
    private DataMapper<Training> mapper;
    private DataReader<Training> reader;
    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;
    private final Map<Long, Training> trainingStorage;

    public TrainingStorage() {
        this.trainingStorage = new TreeMap<>();
    }

    @Autowired
    public void setMapper(DataMapper<Training> mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setReader(DataReader<Training> reader) {
        this.reader = reader;
    }

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @PostConstruct
    public void init() {
        Map<Long, Trainee> trainees = traineeStorage.getTraineeStorage();
        Map<Long, Trainer> trainers = trainerStorage.getTrainerStorage();
        Queue<Training> trainings = reader.read(filePath, mapper);

        trainings.forEach(e -> {
            Long firstKeyTrainee = trainees.keySet().iterator().next();
            Trainee removedTrainee = trainees.remove(firstKeyTrainee);

            Long firstKeyTrainer = trainers.keySet().iterator().next();
            Trainer removedTrainer = trainers.remove(firstKeyTrainer);

            e.setTrainee(removedTrainee);
            e.setTrainer(removedTrainer);
            trainingStorage.put(e.getId(), e);
        });
    }

    public Map<Long, Training> getTrainingStorage() {
        return trainingStorage;
    }
}
