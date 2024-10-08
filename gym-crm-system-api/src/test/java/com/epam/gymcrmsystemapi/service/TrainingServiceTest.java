package com.epam.gymcrmsystemapi.service;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.trainee.TraineeDAO;
import com.epam.gymcrmsystemapi.repository.trainer.TrainerDAO;
import com.epam.gymcrmsystemapi.repository.training.TrainingDAO;
import com.epam.gymcrmsystemapi.service.training.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @InjectMocks
    private TrainingService trainingService;

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private TrainingDAO trainingDAO;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingSaveRequest saveRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trainee = getTrainee();
        trainer = getTrainer();
        training = getTraining();
        saveRequest = new TrainingSaveRequest(
                1L,
                1L,
                "Box",
                TrainingType.STRENGTH,
                OffsetDateTime.now(),
                Duration.parse("PT1H30M"));
    }

    @Test
    void testCreateSuccess() {
        when(traineeDAO.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerDAO.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingDAO.save(any(Training.class))).thenReturn(training);

        TrainingResponse response = trainingService.create(saveRequest);

        assertNotNull(response);
        assertEquals(training.getId(), response.id());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertEquals(training.getTrainingType(), response.trainingType());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
        verify(traineeDAO, only()).findById(1L);
        verify(trainerDAO, only()).findById(1L);
        verify(trainingDAO, only()).save(any(Training.class));
    }

    @Test
    void testCreateTraineeNotFound() {
        when(traineeDAO.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainingService.create(saveRequest));

        assertEquals("404 NOT_FOUND \"Trainee with id '1' not found\"", exception.getMessage());

        verify(trainerDAO, never()).findById(anyLong());
        verify(trainingDAO, never()).save(any(Training.class));
    }

    @Test
    void testCreateTrainerNotFound() {
        when(traineeDAO.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerDAO.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainingService.create(saveRequest));

        assertEquals("404 NOT_FOUND \"Trainer with id '1' not found\"", exception.getMessage());

        verify(traineeDAO, only()).findById(1L);
        verify(trainingDAO, never()).save(any(Training.class));
    }

    @Test
    void testList() {
        Page<Training> trainingPage = new PageImpl<>(Collections.singletonList(training));
        when(trainingDAO.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TrainingResponse> responsePage = trainingService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(training.getId(), responsePage.getContent().get(0).id());
        assertEquals(training.getTrainingName(), responsePage.getContent().get(0).trainingName());
        assertEquals(training.getTrainingType(), responsePage.getContent().get(0).trainingType());
        assertEquals(training.getTrainingDate(), responsePage.getContent().get(0).trainingDate());
        assertEquals(training.getTrainingDuration(), responsePage.getContent().get(0).trainingDuration());
        verify(trainingDAO, only()).findAll(any(Pageable.class));
    }

    @Test
    void testFindById() {
        when(trainingDAO.findById(1L)).thenReturn(Optional.of(training));

        Optional<TrainingResponse> response = trainingService.findById(1L);

        assertTrue(response.isPresent());
        assertEquals(training.getId(), response.get().id());
        assertEquals(training.getTrainingName(), response.get().trainingName());
        assertEquals(training.getTrainingType(), response.get().trainingType());
        assertEquals(training.getTrainingDate(), response.get().trainingDate());
        assertEquals(training.getTrainingDuration(), response.get().trainingDuration());
        verify(trainingDAO, only()).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(trainingDAO.findById(1L)).thenReturn(Optional.empty());

        Optional<TrainingResponse> response = trainingService.findById(1L);

        assertFalse(response.isPresent());
        verify(trainingDAO, only()).findById(1L);
    }

    private Training getTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainee(getTrainee());
        training.setTrainer(getTrainer());
        training.setTrainingName("Training 1");
        training.setTrainingType(TrainingType.STRENGTH);
        training.setTrainingDate(OffsetDateTime.now());
        training.setTrainingDuration(Duration.parse("PT1H30M"));
        return training;
    }

    private Trainee getTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"));
        trainee.setAddress("123 Main St");
        trainee.setUser(getUser());
        return trainee;
    }

    private Trainer getTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization("Box");
        trainer.setUser(getUser());
        return trainer;
    }

    private User getUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
