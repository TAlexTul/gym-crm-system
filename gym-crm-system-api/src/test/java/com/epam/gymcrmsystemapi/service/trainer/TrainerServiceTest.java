package com.epam.gymcrmsystemapi.service.trainer;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.SpecializationRepository;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @InjectMocks
    private TrainerService trainerService;
    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private UserOperations userOperations;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;

    private final static String OLD_PASSWORD = "aB9dE4fGhJ";
    private final static int PASSWORD_STRENGTH = 10;
    private final PasswordEncoder controlEncoder = new BCryptPasswordEncoder(PASSWORD_STRENGTH, new SecureRandom());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_whenSuccess() {
        TrainerSaveRequest request = getTrainerSaveRequest();
        String firstName = request.firstName();
        String lastName = request.lastName();
        Trainer trainer = getTrainer();
        Specialization specialization = trainer.getSpecialization();
        User user = trainer.getUser();

        when(userOperations.save(firstName, lastName)).thenReturn(user);
        when(specializationRepository.findById(request.specializationType())).thenReturn(Optional.of(specialization));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        TrainerRegistrationResponse response = trainerService.create(request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getId(), response.id());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getPassword(), response.password());
        verify(userOperations, only()).save(firstName, lastName);
        verify(specializationRepository, only()).findById(request.specializationType());
        verify(trainerRepository, only()).save(any(Trainer.class));
    }

    @Test
    void testList() {
        Trainer trainer = getTrainer();
        Page<Trainer> trainingPage = new PageImpl<>(Collections.singletonList(trainer));
        when(trainerRepository.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TrainerResponse> responsePage = trainerService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainer.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainer.getUser().getStatus(), responsePage.getContent().get(0).status());
        verify(trainerRepository, only()).findAll(any(Pageable.class));
    }

    @Test
    void testListOfTrainersNotAssignedByTraineeUsername() {
        String username = "John.Doe";
        Trainee trainee = getTrainee();
        Trainer trainer = getTrainer();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllNotAssignedToTrainee(trainee)).thenReturn(List.of(trainer));

        List<TrainerResponse> responses = trainerService.listOfTrainersNotAssignedByTraineeUsername(username);

        assertEquals(1, responses.size());
        assertEquals(trainer.getUser().getFirstName(), responses.get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), responses.get(0).lastName());
        assertEquals(trainer.getUser().getStatus(), responses.get(0).status());
        verify(traineeRepository, only()).findByUsername(username);
        verify(trainerRepository, only()).findAllNotAssignedToTrainee(trainee);
    }

    @Test
    void testListOfTrainersNotAssignedByTraineeUsername_whenTraineeIsNotFound() {
        String username = "John.Doe";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> trainerService.listOfTrainersNotAssignedByTraineeUsername(username));

        assertEquals(
                "404 NOT_FOUND \"Trainee with user name '" + username + "' not found\"", exception.getMessage());

        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Trainer trainer = getTrainer();

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        Optional<TrainerResponse> response = trainerService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(trainer.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainer.getUser().getLastName(), response.get().lastName());
        assertEquals(trainer.getUser().getStatus(), response.get().status());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.get().specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.get().specialization().specializationType());

        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testFindById_whenTrainerIsNotFound() {
        Long id = 1L;

        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TrainerResponse> response = trainerService.findById(id);

        assertFalse(response.isPresent());
        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testFindByUsername() {
        String username = "John.Doe";
        Trainer trainer = getTrainer();

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        Optional<TrainerResponse> response = trainerService.findByUsername(username);

        assertTrue(response.isPresent());
        assertEquals(trainer.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainer.getUser().getLastName(), response.get().lastName());
        assertEquals(trainer.getUser().getStatus(), response.get().status());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.get().specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.get().specialization().specializationType());

        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testFindByUsername_whenTrainerIsNotFound() {
        String username = "John.Doe";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<TrainerResponse> response = trainerService.findByUsername(username);

        assertFalse(response.isPresent());
        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testMergeById() {
        Long id = 1L;
        TrainerMergeRequest request = getTrainerMergeRequest();
        Trainer trainer = getTrainer();

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.mergeById(id, request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getStatus(), response.status());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.specialization().specializationType());

        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testMergeById_whenTrainerIsNotFound() {
        Long id = 1L;
        TrainerMergeRequest request = getTrainerMergeRequest();

        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.mergeById(id, request));

        assertEquals("404 NOT_FOUND \"Trainer with id '" + id + "' not found\"", exception.getMessage());

        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testMergeByUsername() {
        String username = "John.Doe";
        TrainerMergeRequest request = getTrainerMergeRequest();
        Trainer trainer = getTrainer();

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.mergeByUsername(username, request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getStatus(), response.status());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.specialization().specializationType());

        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testMergeByUsername_whenTrainerIsNotFound() {
        String username = "John.Doe";
        TrainerMergeRequest request = getTrainerMergeRequest();

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.mergeByUsername(username, request));

        assertEquals(
                "404 NOT_FOUND \"Trainer with user name '" + username + "' not found\"",
                exception.getMessage());

        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeStatusById() {
        Long id = 1L;
        Trainer trainer = getTrainer();
        var status = UserStatus.ACTIVE;

        doNothing().when(userOperations).changeStatusById(id, status);
        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.changeStatusById(id, status);

        assertNotNull(response);

        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getStatus(), response.status());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.specialization().specializationType());

        verify(userOperations, only()).changeStatusById(id, status);
        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testChangeStatusByUsername() {
        String username = "John.Doe";
        Trainer trainer = getTrainer();
        var status = UserStatus.ACTIVE;

        doNothing().when(userOperations).changeStatusByUsername(username, status);
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.changeStatusByUsername(username, status);

        assertNotNull(response);

        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getStatus(), response.status());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.specialization().specializationType());

        verify(trainerRepository, only()).findByUsername(username);
        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;

        doNothing().when(trainerRepository).deleteById(id);

        trainerService.deleteById(id);

        verify(trainerRepository, only()).deleteById(id);
    }

    @Test
    void testDeleteByUsername() {
        String username = "John.Doe";

        doNothing().when(trainerRepository).deleteByUsername(username);

        trainerService.deleteByUsername(username);

        verify(trainerRepository, only()).deleteByUsername(username);
    }

    private TrainerSaveRequest getTrainerSaveRequest() {
        return new TrainerSaveRequest(
                "John",
                "Doe",
                SpecializationType.PERSONAL_TRAINER
        );
    }

    private TrainerMergeRequest getTrainerMergeRequest() {
        return new TrainerMergeRequest(
                "John.Doe",
                "John",
                "Doe",
                UserStatus.ACTIVE,
                new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER)
        );
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
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword(controlEncoder.encode(OLD_PASSWORD));
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
