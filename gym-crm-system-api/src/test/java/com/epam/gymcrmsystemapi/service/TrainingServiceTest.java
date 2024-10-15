package com.epam.gymcrmsystemapi.service;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import com.epam.gymcrmsystemapi.model.training.Type;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
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

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @InjectMocks
    private TrainingService trainingService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingSaveRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trainee = getTrainee();
        trainer = getTrainer();
        training = getTraining();
        request = new TrainingSaveRequest(
                "John.Doe",
                "Jane.Jenkins",
                "Training 1",
                new TrainingType(Type.CARDIO_WORKOUT, Type.CARDIO_WORKOUT),
                OffsetDateTime.now(),
                300000L);
    }

    @Test
    void testCreateSuccess() {
        String traineeUsername = "John.Doe";
        String trainerUsername = "Jane.Jenkins";

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        TrainingResponse response = trainingService.create(request);

        assertNotNull(response);
        assertEquals(training.getId(), response.id());
        assertEquals(training.getTrainees(), response.trainees());
        assertEquals(training.getTrainers(), response.trainers());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertEquals(training.getTrainingTypes(), response.trainingTypes());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
        verify(traineeRepository, only()).findByUsername(traineeUsername);
        verify(trainerRepository, only()).findByUsername(trainerUsername);
        verify(trainingRepository, only()).save(any(Training.class));
        verifyNoMoreInteractions(traineeRepository);
        verifyNoMoreInteractions(trainerRepository);
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    void testCreateTraineeNotFound() {
        String traineeUsername = "John.Doe";

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainingService.create(request));

        assertEquals("404 NOT_FOUND \"Trainee with user name 'John.Doe' not found\"", exception.getMessage());

        verify(trainerRepository, never()).findByUsername(traineeUsername);
        verify(trainingRepository, never()).save(any(Training.class));
        verifyNoMoreInteractions(trainerRepository);
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    void testCreateTrainerNotFound() {
        String traineeUsername = "John.Doe";
        String trainerUsername = "Jane.Jenkins";

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainingService.create(request));

        assertEquals("404 NOT_FOUND \"Trainer with user name 'Jane.Jenkins' not found\"", exception.getMessage());

        verify(traineeRepository, only()).findByUsername(traineeUsername);
        verify(trainingRepository, never()).save(any(Training.class));
        verifyNoMoreInteractions(traineeRepository);
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    void testList() {
        Page<Training> trainingPage = new PageImpl<>(Collections.singletonList(training));
        when(trainingRepository.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TrainingResponse> responsePage = trainingService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(training.getId(), responsePage.getContent().get(0).id());
        assertEquals(training.getTrainingName(), responsePage.getContent().get(0).trainingName());
        assertEquals(training.getTrainingTypes(), responsePage.getContent().get(0).trainingTypes());
        assertEquals(training.getTrainingDate(), responsePage.getContent().get(0).trainingDate());
        assertEquals(training.getTrainingDuration(), responsePage.getContent().get(0).trainingDuration());
        verify(trainingRepository, only()).findAll(any(Pageable.class));
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    void testListOfTrainersNotAssignedByTraineeUsername() {
        String username = "John.Doe";
        Page<Trainer> trainerPage = new PageImpl<>(Collections.singletonList(trainer));

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllNotAssignedToTrainee(trainee, Pageable.unpaged())).thenReturn(trainerPage);

        Page<TrainerResponse> responsePage = trainingService.listOfTrainersNotAssignedByTraineeUsername(
                username, Pageable.unpaged());

        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainer.getUser().getId(), responsePage.getContent().get(0).userId());
        assertEquals(trainer.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainer.getUser().getUsername(), responsePage.getContent().get(0).userName());
        assertEquals(trainer.getUser().getStatus(), responsePage.getContent().get(0).status());
        assertEquals(trainer.getId(), responsePage.getContent().get(0).trainerId());
        assertEquals(trainer.getSpecialization(), responsePage.getContent().get(0).specialization());
        verify(traineeRepository, only()).findByUsername(username);
        verify(trainerRepository, only()).findAllNotAssignedToTrainee(trainee, Pageable.unpaged());
        verifyNoMoreInteractions(traineeRepository);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testFindById() {
        Long id = 1L;

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));

        Optional<TrainingResponse> response = trainingService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(training.getId(), response.get().id());
        assertEquals(training.getTrainees(), response.get().trainees());
        assertEquals(training.getTrainers(), response.get().trainers());
        assertEquals(training.getTrainingName(), response.get().trainingName());
        assertEquals(training.getTrainingTypes(), response.get().trainingTypes());
        assertEquals(training.getTrainingDate(), response.get().trainingDate());
        assertEquals(training.getTrainingDuration(), response.get().trainingDuration());
        verify(trainingRepository, only()).findById(id);
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    void testFindByIdNotFound() {
        Long id = 1L;

        when(trainingRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TrainingResponse> response = trainingService.findById(id);

        assertFalse(response.isPresent());
        verify(trainingRepository, only()).findById(id);
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    void testUpdateTraineeSetOfTrainers() {
        String username = "Jane.Jenkins";
        Long trainingId = 1L;

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

        TrainingResponse response = trainingService.updateTraineeSetOfTrainers(trainingId, List.of("Jane.Jenkins"));

        assertEquals(training.getId(), response.id());
        assertEquals(training.getTrainees(), response.trainees());
        assertEquals(training.getTrainers(), response.trainers());
        assertEquals(training.getTrainingName(), response.trainingName());
        assertEquals(training.getTrainingTypes(), response.trainingTypes());
        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
        verify(trainerRepository, times(1)).findByUsername(username);
        verify(trainingRepository, times(1)).findById(trainingId);
        verifyNoMoreInteractions(trainerRepository);
        verifyNoMoreInteractions(trainingRepository);
    }

    private Training getTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainees(new HashSet<>(List.of(getTrainee())));
        training.setTrainers(new HashSet<>(List.of(getTrainer())));
        training.setTrainingName("Training 1");
        training.setTrainingTypes(List.of(new TrainingType(Type.CARDIO_WORKOUT, Type.CARDIO_WORKOUT)));
        training.setTrainingDate(OffsetDateTime.now());
        training.setTrainingDuration(300000L);
        return training;
    }

    private Trainee getTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"));
        trainee.setAddress("123 Main St");
        trainee.setUser(getUser1());
        return trainee;
    }

    private Trainer getTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization(Specialization.PERSONAL_TRAINER);
        trainer.setUser(getUser2());
        return trainer;
    }

    private User getUser1() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Jenkins");
        user.setUsername("Jane.Jenkins");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUser2() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
