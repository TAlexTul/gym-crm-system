package com.epam.gymcrmsystemapi.repository.storage;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.storage.mapper.DataMapper;
import com.epam.gymcrmsystemapi.repository.storage.reader.DataReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainingStorageTest {

    @InjectMocks
    private TrainingStorage trainingStorage;

    @Mock
    private DataMapper<Training> mapper;

    @Mock
    private DataReader<Training> reader;

    @Mock
    private TraineeStorage traineeStorage;

    @Mock
    private TrainerStorage trainerStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(trainingStorage, "filePath", "training.csv");
        trainingStorage.setMapper(mapper);
        trainingStorage.setReader(reader);
        trainingStorage.setTraineeStorage(traineeStorage);
        trainingStorage.setTrainerStorage(trainerStorage);
    }

    @Test
    void testInit() {
        Trainee trainee1 = getTrainee1();
        Trainee trainee2 = getTrainee2();
        Map<Long, Trainee> trainees = new TreeMap<>();
        trainees.put(trainee1.getId(), trainee1);
        trainees.put(trainee2.getId(), trainee2);

        Trainer trainer1 = getTrainer1();
        Trainer trainer2 = getTrainer2();
        Map<Long, Trainer> trainers = new TreeMap<>();
        trainers.put(trainer1.getId(), trainer1);
        trainers.put(trainer2.getId(), trainer2);

        Training training1 = getTraining1();
        Training training2 = getTraining2();
        Queue<Training> trainingQueue = new LinkedList<>();
        trainingQueue.add(training1);
        trainingQueue.add(training2);

        when(traineeStorage.getTraineeStorage()).thenReturn(trainees);
        when(trainerStorage.getTrainerStorage()).thenReturn(trainers);
        when(reader.read(anyString(), any(DataMapper.class))).thenReturn(trainingQueue);

        trainingStorage.init();

        Map<Long, Training> storage = trainingStorage.getTrainingStorage();
        assertEquals(2, storage.size());
        assertEquals(training1, storage.get(1L));
        assertEquals(training2, storage.get(2L));

        verify(reader).read(anyString(), any(DataMapper.class));
    }

    private Training getTraining1() {
        var training1 = new Training();
        training1.setId(1L);
        training1.setTrainee(getTrainee1());
        training1.setTrainer(getTrainer1());
        training1.setTrainingName("Training 1");
        training1.setTrainingType(TrainingType.STRENGTH);
        training1.setTrainingDate(OffsetDateTime.now().plusDays(5));
        training1.setTrainingDuration(Duration.parse("PT1H30M"));
        return training1;
    }

    private Training getTraining2() {
        var training2 = new Training();
        training2.setId(2L);
        training2.setTrainee(getTrainee2());
        training2.setTrainer(getTrainer2());
        training2.setTrainingName("Training 2");
        training2.setTrainingType(TrainingType.CARDIO);
        training2.setTrainingDate(OffsetDateTime.now().plusDays(5L));
        training2.setTrainingDuration(Duration.parse("PT2H30M"));
        return training2;
    }

    private Trainer getTrainer1() {
        var trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setSpecialization("Box");
        trainer1.setUser(getUser1());
        return trainer1;
    }

    private Trainer getTrainer2() {
        var trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setSpecialization("Football");
        trainer2.setUser(getUser2());
        return trainer2;
    }

    private Trainee getTrainee1() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.now());
        trainee.setAddress("123 Main St");
        trainee.setUser(getUser1());
        return trainee;
    }

    private Trainee getTrainee2() {
        Trainee trainee = new Trainee();
        trainee.setId(2L);
        trainee.setDateOfBirth(OffsetDateTime.now().plusDays(1L));
        trainee.setAddress("101 Independence Avenue");
        trainee.setUser(getUser2());
        return trainee;
    }

    private User getUser1() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUser2() {
        var user = new User();
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUserName("Jane.Smith");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
