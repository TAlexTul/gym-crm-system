package com.epam.gymcrmsystemapi.repository.storage;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
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
public class TrainerStorage {

    @Value("${storage.file.path.trainer}")
    private String filePath;
    private DataMapper<Trainer> mapper;
    private DataReader<Trainer> reader;
    private final Map<Long, Trainer> trainerStorage;

    public TrainerStorage() {
        this.trainerStorage = new TreeMap<>();
    }

    @Autowired
    public void setMapper(DataMapper<Trainer> mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setReader(DataReader<Trainer> reader) {
        this.reader = reader;
    }

    @PostConstruct
    public void init() {
        Queue<Trainer> entities = reader.read(filePath, mapper);
        entities.forEach(e -> trainerStorage.put(e.getId(), e));
    }

    public Map<Long, Trainer> getTrainerStorage() {
        return trainerStorage;
    }
}
