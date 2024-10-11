package com.epam.gymcrmsystemapi.repository.storage;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
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
public class TraineeStorage {

    @Value("${storage.file.path.trainee}")
    private String filePath;
    private DataMapper<Trainee> mapper;
    private DataReader<Trainee> reader;
    private final Map<Long, Trainee> traineeStorage;

    public TraineeStorage() {
        traineeStorage = new TreeMap<>();
    }

    @Autowired
    public void setMapper(DataMapper<Trainee> mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setReader(DataReader<Trainee> reader) {
        this.reader = reader;
    }

    @PostConstruct
    public void init() {
        Queue<Trainee> entities = reader.read(filePath, mapper);
        entities.forEach(e -> traineeStorage.put(e.getId(), e));
    }

    public Map<Long, Trainee> getTraineeStorage() {
        return traineeStorage;
    }
}
