package com.epam.gymcrmsystemapi.repository.storage;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
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

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TraineeStorageTest {

    @InjectMocks
    private TraineeStorage traineeStorage;

    @Mock
    private DataMapper<Trainee> mapper;

    @Mock
    private DataReader<Trainee> reader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(traineeStorage, "filePath", "trainee.csv");
        traineeStorage.setMapper(mapper);
        traineeStorage.setReader(reader);
    }

    @Test
    void testInit() {
        Trainee trainee1 = getTrainee1();
        Trainee trainee2 = getTrainee2();
        Queue<Trainee> traineeQueue = new LinkedList<>();
        traineeQueue.add(trainee1);
        traineeQueue.add(trainee2);

        when(reader.read(anyString(), any(DataMapper.class))).thenReturn(traineeQueue);

        traineeStorage.init();

        Map<Long, Trainee> storage = traineeStorage.getTraineeStorage();
        assertEquals(2, storage.size());
        assertEquals(trainee1, storage.get(1L));
        assertEquals(trainee2, storage.get(2L));

        verify(reader).read(anyString(), any(DataMapper.class));
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
