package com.epam.gymcrmsystemapi.service.training;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
import com.epam.gymcrmsystemapi.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingRepository trainingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_whenSuccess() {
        TrainingSaveRequest request = getTrainingSaveRequest();
        String traineeUsername = "John.Doe";
        String trainerUsername = "Jane.Jenkins";
        Trainee trainee = getTrainee();
        Trainer trainer = getTrainer();
        Training training = getTraining();

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(request.trainingType())).thenReturn(
                Optional.of(new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING)));
        when(trainingRepository.findByTrainingNameLike(request.trainingName())).thenReturn(Optional.of(training));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        TrainingResponse response = trainingService.create(request);

        assertNotNull(response);
        assertEquals(training.getId(), response.id());

        assertEquals(training.getTrainees().iterator().next().getAddress(),
                response.trainees().iterator().next().address());
        assertEquals(training.getTrainees().iterator().next().getDateOfBirth(),
                response.trainees().iterator().next().dateOfBirth());

        assertEquals(training.getTrainers().iterator().next().getSpecialization().getId().ordinal(),
                response.trainers().iterator().next().specialization().id());
        assertEquals(training.getTrainers().iterator().next().getSpecialization().getSpecialization(),
                response.trainers().iterator().next().specialization().specializationType());

        assertEquals(training.getTrainingName(), response.trainingName());

        assertEquals(training.getTrainingTypes().get(0).getId().ordinal(), response.trainingTypes().get(0).id());
        assertEquals(training.getTrainingTypes().get(0).getType(), response.trainingTypes().get(0).type());

        assertEquals(training.getTrainingDate(), response.trainingDate());
        assertEquals(training.getTrainingDuration(), response.trainingDuration());
        verify(traineeRepository, only()).findByUsername(traineeUsername);
        verify(trainerRepository, only()).findByUsername(trainerUsername);
        verify(trainingTypeRepository, only()).findById(request.trainingType());
        verify(trainingRepository, times(1)).findByTrainingNameLike(request.trainingName());
        verify(trainingRepository, times(1)).save(any(Training.class));
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    void testList() {
        Training training = getTraining();
        Page<Training> trainingPage = new PageImpl<>(Collections.singletonList(training));
        when(trainingRepository.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TrainingResponse> responsePage = trainingService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(training.getId(), responsePage.getContent().get(0).id());
        assertEquals(training.getTrainingName(), responsePage.getContent().get(0).trainingName());
        assertEquals(training.getTrainingDate(), responsePage.getContent().get(0).trainingDate());
        assertEquals(training.getTrainingDuration(), responsePage.getContent().get(0).trainingDuration());
        verify(trainingRepository, only()).findAll(any(Pageable.class));
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Training training = getTraining();

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));

        Optional<TrainingResponse> response = trainingService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(training.getId(), response.get().id());

        assertEquals(training.getTrainees().iterator().next().getAddress(),
                response.get().trainees().iterator().next().address());
        assertEquals(training.getTrainees().iterator().next().getDateOfBirth(),
                response.get().trainees().iterator().next().dateOfBirth());

        assertEquals(training.getTrainers().iterator().next().getSpecialization().getId().ordinal(),
                response.get().trainers().iterator().next().specialization().id());
        assertEquals(training.getTrainers().iterator().next().getSpecialization().getSpecialization(),
                response.get().trainers().iterator().next().specialization().specializationType());

        assertEquals(training.getTrainingName(), response.get().trainingName());

        assertEquals(training.getTrainingTypes().get(0).getId().ordinal(), response.get().trainingTypes().get(0).id());
        assertEquals(training.getTrainingTypes().get(0).getType(), response.get().trainingTypes().get(0).type());

        assertEquals(training.getTrainingDate(), response.get().trainingDate());
        assertEquals(training.getTrainingDuration(), response.get().trainingDuration());
        verify(trainingRepository, only()).findById(id);
    }

    @Test
    void testFindById_whenTrainingIsNotFound() {
        Long id = 1L;

        when(trainingRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TrainingResponse> response = trainingService.findById(id);

        assertFalse(response.isPresent());
        verify(trainingRepository, only()).findById(id);
    }

    @Test
    void shouldFilterTrainingsByCriteria() {
        Training training = getTraining();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Training> page = new PageImpl<>(Collections.singletonList(training));

        when(trainingRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        String traineeUsername = training.getTrainees().stream().findFirst().orElseThrow().getUser().getUsername();
        String trainerUsername = training.getTrainers().stream().findFirst().orElseThrow().getUser().getUsername();
        OffsetDateTime fromDate = OffsetDateTime.now().minusDays(1);
        OffsetDateTime toDate = OffsetDateTime.now().plusDays(1);

        Page<TrainingResponse> responses = trainingService.filterBy(
                traineeUsername, trainerUsername, fromDate, toDate, Type.STRENGTH_TRAINING, pageable
        );

        assertEquals(1, responses.getTotalElements());
        assertEquals(1, responses.getContent().size());
        assertEquals(training.getId(), responses.getContent().get(0).id());
        assertEquals(training.getTrainingName(), responses.getContent().get(0).trainingName());
        assertEquals(training.getTrainingDate(), responses.getContent().get(0).trainingDate());
        assertEquals(training.getTrainingDuration(), responses.getContent().get(0).trainingDuration());
        verify(trainingRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldReturnEmptyPageWhenNoTrainingsMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Training> emptyPage = new PageImpl<>(Collections.emptyList());

        when(trainingRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        String traineeUsername = "traineeTest";
        String trainerUsername = "trainerTest";
        OffsetDateTime fromDate = OffsetDateTime.now().minusDays(1);
        OffsetDateTime toDate = OffsetDateTime.now().plusDays(1);

        Page<TrainingResponse> result = trainingService.filterBy(
                traineeUsername, trainerUsername, fromDate, toDate, Type.STRENGTH_TRAINING, pageable
        );

        assertTrue(result.isEmpty());
        verify(trainingRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldDeleteTrainingByIdAndReturnResponse() {
        long id = 1L;
        Training training = getTraining();

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));
        doNothing().when(trainingRepository).delete(training);

        Optional<TrainingResponse> response = trainingService.deleteById(id);

        assertTrue(response.isPresent());
        assertEquals(training.getId(), response.get().id());

        assertEquals(training.getTrainees().iterator().next().getAddress(),
                response.get().trainees().iterator().next().address());
        assertEquals(training.getTrainees().iterator().next().getDateOfBirth(),
                response.get().trainees().iterator().next().dateOfBirth());

        assertEquals(training.getTrainers().iterator().next().getSpecialization().getId().ordinal(),
                response.get().trainers().iterator().next().specialization().id());
        assertEquals(training.getTrainers().iterator().next().getSpecialization().getSpecialization(),
                response.get().trainers().iterator().next().specialization().specializationType());

        assertEquals(training.getTrainingName(), response.get().trainingName());

        assertEquals(training.getTrainingTypes().get(0).getId().ordinal(), response.get().trainingTypes().get(0).id());
        assertEquals(training.getTrainingTypes().get(0).getType(), response.get().trainingTypes().get(0).type());

        assertEquals(training.getTrainingDate(), response.get().trainingDate());
        assertEquals(training.getTrainingDuration(), response.get().trainingDuration());
        verify(trainingRepository, times(1)).findById(id);
        verify(trainingRepository, times(1)).delete(training);
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainingNotFound() {
        long id = 1L;
        Training training = getTraining();

        when(trainingRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TrainingResponse> response = trainingService.deleteById(id);

        verify(trainingRepository, never()).delete(training);

        assertFalse(response.isPresent());
    }

    private TrainingSaveRequest getTrainingSaveRequest() {
        return new TrainingSaveRequest(
                "John.Doe",
                "Jane.Jenkins",
                "Training 1",
                Type.STRENGTH_TRAINING,
                OffsetDateTime.now(),
                300000L);
    }

    private Training getTraining() {
        List<TrainingType> trainingTypes = new ArrayList<>();
        trainingTypes.add(new TrainingType(Type.CARDIO_WORKOUT, Type.CARDIO_WORKOUT));

        var training = new Training();
        training.setId(1L);
        training.setTrainees(new HashSet<>(List.of(getTrainee())));
        training.setTrainers(new HashSet<>(List.of(getTrainer())));
        training.setTrainingName("Training 1");
        training.setTrainingTypes(trainingTypes);
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
        trainer.setSpecialization(
                new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER));
        trainer.setUser(getUser2());
        return trainer;
    }

    private User getUser1() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Jenkins");
        user.setUsername("Jane.Jenkins");
        user.setPassword(null);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUser2() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword(null);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
