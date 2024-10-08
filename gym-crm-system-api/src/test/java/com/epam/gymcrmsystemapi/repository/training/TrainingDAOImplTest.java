package com.epam.gymcrmsystemapi.repository.training;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainingDAOImplTest {

    @InjectMocks
    private TrainingDAOImpl trainingDAO;

    @Mock
    private TrainingStorage trainingStorage;

    @Mock
    private Map<Long, Training> trainingMap;

    private Training training1;

    private Training training2;

    private Training training3;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(trainingStorage.getTrainingStorage()).thenReturn(trainingMap);
        training1 = getTraining1();
        training2 = getTraining2();
        training3 = getTraining3();
    }

    @Test
    public void testSave() {
        training1.setId(null);

        when(trainingMap.values()).thenReturn(new ArrayList<>());
        when(trainingMap.put(any(Long.class), any(Training.class))).thenReturn(training1);

        Training entity = trainingDAO.save(training1);

        assertEquals(1L, entity.getId());
        verify(trainingMap, times(1)).values();
        verify(trainingMap, times(1)).put(1L, this.training1);
    }

    @Test
    public void testSave_StorageNotEmpty() {
        Map<Long, Training> trainings = new TreeMap<>();
        trainings.put(training2.getId(), training2);
        trainings.put(training3.getId(), training3);
        training1.setId(null);

        when(trainingMap.values()).thenReturn(trainings.values());
        when(trainingMap.put(any(Long.class), any(Training.class))).thenReturn(training1);

        Training entity = trainingDAO.save(training1);

        assertEquals(4L, entity.getId());
        verify(trainingMap, times(1)).values();
        verify(trainingMap, times(1)).put(4L, training1);
    }

    @Test
    public void testList() {
        Map<Long, Training> trainers = new TreeMap<>();
        trainers.put(training1.getId(), training1);
        trainers.put(training2.getId(), training2);
        trainers.put(training3.getId(), training3);

        int pageNumber = 0;
        int pageSize = 2;
        PageRequest trainerPage = PageRequest.of(pageNumber, pageSize);

        when(trainingMap.values()).thenReturn(trainers.values());

        Page<Training> responsePage = trainingDAO.findAll(trainerPage);

        assertNotNull(responsePage);
        assertEquals(3, responsePage.getTotalElements());
        assertEquals(0, responsePage.getNumber());
        assertEquals(2, responsePage.getTotalPages());
        assertEquals(2, responsePage.getContent().size());
        verify(trainingMap, times(1)).values();
    }

    @Test
    public void testFindById() {
        when(trainingMap.get(1L)).thenReturn(training1);

        Optional<Training> response = trainingDAO.findById(1L);

        assertTrue(response.isPresent());
        assertEquals(training1.getId(), response.get().getId());
        assertEquals(training1.getTrainingName(), response.get().getTrainingName());
        assertEquals(training1.getTrainingType(), response.get().getTrainingType());
        assertEquals(training1.getTrainingDate(), response.get().getTrainingDate());
        assertEquals(training1.getTrainingDuration(), response.get().getTrainingDuration());
        verify(trainingMap, only()).get(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(trainingMap.get(1L)).thenReturn(null);

        Optional<Training> response = trainingDAO.findById(1L);

        assertFalse(response.isPresent());
        verify(trainingMap, only()).get(1L);
    }

    private Training getTraining1() {
        var training1 = new Training();
        training1.setId(1L);
        training1.setTrainee(getTrainee1());
        training1.setTrainer(getTrainer1());
        training1.setTrainingName("Training 1");
        training1.setTrainingType(TrainingType.STRENGTH);
        training1.setTrainingDate(OffsetDateTime.now().plusMonths(1L));
        training1.setTrainingDuration(Duration.parse("PT1H30M"));
        return training1;
    }

    private Training getTraining2() {
        var training1 = new Training();
        training1.setId(2L);
        training1.setTrainee(getTrainee2());
        training1.setTrainer(getTrainer2());
        training1.setTrainingName("Training 2");
        training1.setTrainingType(TrainingType.STRENGTH);
        training1.setTrainingDate(OffsetDateTime.now().plusMonths(2L));
        training1.setTrainingDuration(Duration.parse("PT2H30M"));
        return training1;
    }

    private Training getTraining3() {
        var training2 = new Training();
        training2.setId(3L);
        training2.setTrainee(getTrainee3());
        training2.setTrainer(getTrainer3());
        training2.setTrainingName("Training 3");
        training2.setTrainingType(TrainingType.CARDIO);
        training2.setTrainingDate(OffsetDateTime.now().plusMonths(3L));
        training2.setTrainingDuration(Duration.parse("PT3H30M"));
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
        var trainer1 = new Trainer();
        trainer1.setId(2L);
        trainer1.setSpecialization("Football");
        trainer1.setUser(getUser2());
        return trainer1;
    }

    private Trainer getTrainer3() {
        var trainer2 = new Trainer();
        trainer2.setId(3L);
        trainer2.setSpecialization("Volleyball");
        trainer2.setUser(getUser3());
        return trainer2;
    }

    private Trainee getTrainee1() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.now());
        trainee.setAddress("123 Main St");
        trainee.setUser(getUser1());
        return trainee;
    }

    private Trainee getTrainee2() {
        var trainee = new Trainee();
        trainee.setId(2L);
        trainee.setDateOfBirth(OffsetDateTime.now().plusDays(2L));
        trainee.setAddress("101 Independence Avenue");
        trainee.setUser(getUser2());
        return trainee;
    }

    private Trainee getTrainee3() {
        var trainee = new Trainee();
        trainee.setId(3L);
        trainee.setDateOfBirth(OffsetDateTime.now().plusDays(1L));
        trainee.setAddress("4 Brain St");
        trainee.setUser(getUser3());
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

    private User getUser3() {
        var user = new User();
        user.setId(3L);
        user.setFirstName("Emily");
        user.setLastName("Johnson");
        user.setUserName("Emily.Johnson");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
