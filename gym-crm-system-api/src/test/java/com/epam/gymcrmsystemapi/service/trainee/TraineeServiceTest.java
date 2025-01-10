package com.epam.gymcrmsystemapi.service.trainee;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeChangeTrainersSetRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
import com.epam.gymcrmsystemapi.service.user.UserOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @InjectMocks
    private TraineeService traineeService;
    @Mock
    private UserOperations userOperations;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;

    private final static String OLD_PASSWORD = "aB9dE4fGhJ";
    private final static int PASSWORD_STRENGTH = 10;
    private final PasswordEncoder controlEncoder = new BCryptPasswordEncoder(PASSWORD_STRENGTH, new SecureRandom());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_whenSuccess() {
        TraineeSaveRequest request = getSaveRequest();
        String firstName = request.firstName();
        String lastName = request.lastName();
        Trainee trainee = getTrainee();
        User user = trainee.getUser();

        when(userOperations.save(firstName, lastName, KnownAuthority.ROLE_TRAINEE)).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        TraineeRegistrationResponse response = traineeService.create(request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getId(), response.id());
        assertEquals(trainee.getUser().getUsername(), response.username());
        assertEquals(trainee.getUser().getPassword(), response.password());
        verify(userOperations, only()).save(firstName, lastName, KnownAuthority.ROLE_TRAINEE);
        verify(traineeRepository, only()).save(any(Trainee.class));
    }

    @Test
    void testList() {
        Trainee trainee = getTrainee();
        Page<Trainee> traineePage = new PageImpl<>(Collections.singletonList(trainee));
        when(traineeRepository.findAll(any(Pageable.class))).thenReturn(traineePage);

        Page<TraineeResponse> responsePage = traineeService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainee.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainee.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainee.getUser().getStatus(), responsePage.getContent().get(0).status());
        assertEquals(trainee.getDateOfBirth(), responsePage.getContent().get(0).dateOfBirth());
        assertEquals(trainee.getAddress(), responsePage.getContent().get(0).address());
        verify(traineeRepository, only()).findAll(any(Pageable.class));
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Trainee trainee = getTrainee();

        when(traineeRepository.findById(id)).thenReturn(Optional.of(trainee));

        Optional<TraineeResponse> response = traineeService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(trainee.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainee.getUser().getLastName(), response.get().lastName());
        assertEquals(trainee.getUser().getStatus(), response.get().status());
        assertEquals(trainee.getDateOfBirth(), response.get().dateOfBirth());
        assertEquals(trainee.getAddress(), response.get().address());
        verify(traineeRepository, only()).findById(id);
    }

    @Test
    void testFindById_whenTraineeIsNotFound() {
        Long id = 1L;

        when(traineeRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TraineeResponse> response = traineeService.findById(id);

        assertFalse(response.isPresent());
        verify(traineeRepository, only()).findById(id);
    }

    @Test
    void testFindByUsername() {
        String username = "John.Doe";
        Trainee trainee = getTrainee();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        Optional<TraineeResponse> response = traineeService.findByUsername(username);

        assertTrue(response.isPresent());
        assertEquals(trainee.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainee.getUser().getLastName(), response.get().lastName());
        assertEquals(trainee.getUser().getStatus(), response.get().status());
        assertEquals(trainee.getDateOfBirth(), response.get().dateOfBirth());
        assertEquals(trainee.getAddress(), response.get().address());
        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testFindByUsername_whenTraineeIsNotFound() {
        String username = "John.Doe";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<TraineeResponse> response = traineeService.findByUsername(username);

        assertFalse(response.isPresent());
        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testMergeById() {
        Long id = 1L;
        TraineeMergeRequest request = getMergeRequest();
        Trainee trainee = getTrainee();

        when(traineeRepository.findById(id)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.mergeById(id, request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeRepository, only()).findById(id);
    }

    @Test
    void testMergeById_whenTraineeIsNotFound() {
        Long id = 1L;
        TraineeMergeRequest request = getMergeRequest();

        when(traineeRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.mergeById(id, request));

        assertEquals("404 NOT_FOUND \"Trainee with id '" + id + "' not found\"", exception.getMessage());

        verify(traineeRepository, only()).findById(id);
    }

    @Test
    void testMergeByUsername() {
        String username = "John.Doe";
        TraineeMergeRequest request = getMergeRequest();
        Trainee trainee = getTrainee();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.mergeByUsername(username, request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testMergeByUsername_whenTraineeIsNotFound() {
        String username = "John.Doe";
        TraineeMergeRequest request = getMergeRequest();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> traineeService.mergeByUsername(username, request));

        assertEquals(
                "404 NOT_FOUND \"Trainee with user name '" + username + "' not found\"",
                exception.getMessage());

        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeStatusById() {
        long id = 1L;
        Trainee trainee = getTrainee();
        var status = UserStatus.ACTIVE;

        doNothing().when(userOperations).changeStatusById(id, status);
        when(traineeRepository.findById(id)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.changeStatusById(id, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, only()).findById(id);
    }

    @Test
    void testChangeStatusById_whenStatusIsNotEqual() {
        Long id = 1L;
        Trainee trainee = getTrainee();
        UserStatus status = UserStatus.SUSPENDED;

        when(traineeRepository.findById(id)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.changeStatusById(id, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, only()).findById(id);
    }

    @Test
    void testChangeStatusByUsername_whenStatusIsEqual() {
        String username = "John.Doe";
        Trainee trainee = getTrainee();
        UserStatus status = UserStatus.ACTIVE;

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeStatusByUsername_whenStatusIsNotEqual() {
        String username = "John.Doe";
        UserStatus status = UserStatus.SUSPENDED;
        Trainee trainee = getTrainee();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeTraineeSetOfTrainers() {
        TraineeChangeTrainersSetRequest request = new TraineeChangeTrainersSetRequest(
                "John.Doe",
                List.of("Jane.Jenkins")
        );
        String traineeUsername = request.traineeUsername();
        String trainerUsername = request.trainerUsernames().get(0);
        Trainee trainee = getTrainee();
        Trainer trainer = getTrainer();

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));

        List<TrainerResponse> response = traineeService.changeTraineeSetOfTrainers(request);

        assertEquals(trainer.getUser().getFirstName(), response.get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), response.get(0).lastName());
        assertEquals(trainer.getUser().getStatus(), response.get(0).status());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.get(0).specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.get(0).specialization().specializationType());

        verify(trainerRepository, only()).findByUsername(trainerUsername);
    }

    @Test
    void testChangeTraineeSetOfTrainers_whenTraineeIsNotFound() {
        TraineeChangeTrainersSetRequest request = new TraineeChangeTrainersSetRequest(
                "John.Doe",
                List.of("Jane.Jenkins")
        );
        String traineeUsername = request.traineeUsername();

        when(traineeRepository.findByUsername(request.traineeUsername())).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> traineeService.changeTraineeSetOfTrainers(request));

        assertEquals(
                "404 NOT_FOUND \"Trainee with user name '" + traineeUsername + "' not found\"",
                exception.getMessage());

        verify(traineeRepository, only()).findByUsername(traineeUsername);
    }

    @Test
    void testChangeTraineeSetOfTrainers_whenTrainerIsNotFound() {
        TraineeChangeTrainersSetRequest request = new TraineeChangeTrainersSetRequest(
                "John.Doe",
                List.of("Jane.Jenkins")
        );
        String traineeUsername = request.traineeUsername();
        String trainerUsername = request.trainerUsernames().get(0);
        Trainee trainee = getTrainee();

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(request.traineeUsername())).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> traineeService.changeTraineeSetOfTrainers(request));

        assertEquals(
                "404 NOT_FOUND \"Trainer with user name '" + trainerUsername + "' not found\"",
                exception.getMessage());

        verify(trainerRepository, only()).findByUsername(trainerUsername);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;
        Training training = getTraining();
        training.getTrainers().remove(getTrainer());
        Trainee trainee = getTrainee();

        when(traineeRepository.findById(id)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTrainees(trainee)).thenReturn(List.of(training));
        doNothing().when(trainingRepository).delete(training);
        doNothing().when(traineeRepository).deleteById(id);

        traineeService.deleteById(id);

        verify(traineeRepository, times(1)).findById(id);
        verify(trainingRepository, times(1)).findAllByTrainees(trainee);
        verify(trainingRepository, times(1)).delete(training);
        verify(traineeRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testDeleteById_whenTraineeIsNotFound() {
        Long id = 1L;
        Training training = getTraining();
        training.getTrainers().remove(getTrainer());

        when(traineeRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.deleteById(id));

        assertEquals("404 NOT_FOUND \"Trainee with id '" + id + "' not found\"", exception.getMessage());

        verify(traineeRepository, only()).findById(id);
    }

    @Test
    void testDeleteByUsername() {
        String username = "John.Doe";
        Training training = getTraining();
        training.getTrainers().remove(getTrainer());
        Trainee trainee = getTrainee();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTrainees(trainee)).thenReturn(List.of(training));
        doNothing().when(trainingRepository).delete(training);
        doNothing().when(traineeRepository).deleteByUsername(username);

        traineeService.deleteByUsername(username);

        verify(traineeRepository, times(1)).findByUsername(username);
        verify(trainingRepository, times(1)).findAllByTrainees(trainee);
        verify(trainingRepository, times(1)).delete(training);
        verify(traineeRepository, times(1)).deleteByUsername(username);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testDeleteByUsername_whenTraineeIsNotFound() {
        String username = "John.Doe";
        Training training = getTraining();
        training.getTrainers().remove(getTrainer());

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.deleteByUsername(username));

        assertEquals(
                "404 NOT_FOUND \"Trainee with user name '" + username + "' not found\"",
                exception.getMessage());

        verify(traineeRepository, only()).findByUsername(username);
    }

    private TraineeSaveRequest getSaveRequest() {
        return new TraineeSaveRequest(
                "John",
                "Doe",
                OffsetDateTime.parse("2007-12-03T10:15:30+01:00"),
                "123 Main St"
        );
    }

    private TraineeMergeRequest getMergeRequest() {
        return new TraineeMergeRequest(
                "John.Doe",
                "John",
                "Doe",
                OffsetDateTime.parse("2007-12-03T10:15:30+01:00"),
                "123 Main St",
                UserStatus.ACTIVE
        );
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
        trainee.setUser(getUserForTrainee());
        return trainee;
    }

    private Trainer getTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization(
                new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER));
        trainer.setUser(getUserForTrainer());
        return trainer;
    }

    private User getUserForTrainee() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword(controlEncoder.encode(OLD_PASSWORD));
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUserForTrainer() {
        var user = new User();
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Jenkins");
        user.setUsername("Jane.Jenkins");
        user.setPassword(controlEncoder.encode(OLD_PASSWORD));
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
