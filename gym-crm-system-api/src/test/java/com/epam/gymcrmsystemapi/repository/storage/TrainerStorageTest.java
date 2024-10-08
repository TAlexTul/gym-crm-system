package com.epam.gymcrmsystemapi.repository.storage;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
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

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrainerStorageTest {

    @InjectMocks
    private TrainerStorage trainerStorage;

    @Mock
    private DataMapper<Trainer> mapper;

    @Mock
    private DataReader<Trainer> reader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(trainerStorage, "filePath", "trainer.csv");
        trainerStorage.setMapper(mapper);
        trainerStorage.setReader(reader);
    }

    @Test
    void testInit() {
        Trainer trainer1 = getTrainer1();
        Trainer trainer2 = getTrainer2();
        Queue<Trainer> trainerQueue = new LinkedList<>();
        trainerQueue.add(trainer1);
        trainerQueue.add(trainer2);

        when(reader.read(anyString(), any(DataMapper.class))).thenReturn(trainerQueue);

        trainerStorage.init();

        Map<Long, Trainer> storage = trainerStorage.getTrainerStorage();
        assertEquals(2, storage.size());
        assertEquals(trainer1, storage.get(1L));
        assertEquals(trainer2, storage.get(2L));

        verify(reader).read(anyString(), any(DataMapper.class));
    }

    private Trainer getTrainer1() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization("Box");
        trainer.setUser(getUser1());
        return trainer;
    }

    private Trainer getTrainer2() {
        var trainer = new Trainer();
        trainer.setId(2L);
        trainer.setSpecialization("Football");
        trainer.setUser(getUser2());
        return trainer;
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
