package com.epam.gymcrmsystemapi.service;

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
import com.epam.gymcrmsystemapi.repository.UserRepository;
import com.epam.gymcrmsystemapi.service.trainer.TrainerService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
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
    private UserRepository userRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private final static int PASSWORD_LENGTH = 10;
    private final static String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private final static String OLD_PASSWORD = "aB9dE4fGhJ";
    private final static String NEW_PASSWORD = "cM5dU4fEhL";
    private final static int PASSWORD_STRENGTH = 10;
    private final PasswordEncoder controlEncoder = new BCryptPasswordEncoder(PASSWORD_STRENGTH, new SecureRandom());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(trainerService, "passwordLength", PASSWORD_LENGTH);
        ReflectionTestUtils.setField(trainerService, "passwordCharacters", PASSWORD_CHARACTERS);
    }

    @Test
    void testCreate_whenSuccess() {
        TrainerSaveRequest request = getTrainerSaveRequest();
        String firstName = request.firstName();
        String lastName = request.lastName();
        String username = String.join(".", firstName, lastName);
        Trainer trainer = getTrainer();
        Specialization specialization = trainer.getSpecialization();
        User user = trainer.getUser();

        when(trainerRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(false);
        when(traineeRepository.existsByUsername(username)).thenReturn(false);
        when(specializationRepository.findById(request.specializationType())).thenReturn(Optional.of(specialization));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        TrainerRegistrationResponse response = trainerService.create(request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getId(), response.id());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getPassword(), response.password());
        verify(trainerRepository, times(1)).existsByFirstNameAndLastName(firstName, lastName);
        verify(traineeRepository, only()).existsByUsername(username);
        verify(userRepository, only()).save(any(User.class));
        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testPrivateCalculateUsername() throws Exception {
        TrainerSaveRequest saveRequest = getTrainerSaveRequest();
        String firstName = saveRequest.firstName();
        String lastName = saveRequest.lastName();
        Trainer trainer = getTrainer();

        when(trainerRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(true);
        when(trainerRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(Optional.of(trainer));

        Method method = TrainerService.class.getDeclaredMethod("calculateUsername", String.class, String.class);
        method.setAccessible(true);

        String userName = (String) method.invoke(trainerService, firstName, lastName);

        assertNotNull(userName);
        assertEquals("John.Doe.1", userName);
        verify(trainerRepository, times(1)).existsByFirstNameAndLastName(firstName, lastName);
        verify(trainerRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testPrivateGenerateRandomPassword() throws Exception {
        Method method = TrainerService.class.getDeclaredMethod("generateRandomPassword");
        method.setAccessible(true);

        String password = (String) method.invoke(trainerService);

        assertNotNull(password);
        assertEquals(PASSWORD_LENGTH, password.length());
        for (char c : password.toCharArray()) {
            assertTrue(PASSWORD_CHARACTERS.contains(String.valueOf(c)));
        }
    }

    @Test
    void testList() {
        Trainer trainer = getTrainer();
        Page<Trainer> trainingPage = new PageImpl<>(Collections.singletonList(trainer));
        when(trainerRepository.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TrainerResponse> responsePage = trainerService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainer.getUser().getId(), responsePage.getContent().get(0).userId());
        assertEquals(trainer.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainer.getUser().getUsername(), responsePage.getContent().get(0).username());
        assertEquals(trainer.getUser().getStatus(), responsePage.getContent().get(0).status());
        assertEquals(trainer.getId(), responsePage.getContent().get(0).trainerId());
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
        assertEquals(trainer.getUser().getId(), responses.get(0).userId());
        assertEquals(trainer.getUser().getFirstName(), responses.get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), responses.get(0).lastName());
        assertEquals(trainer.getUser().getUsername(), responses.get(0).username());
        assertEquals(trainer.getUser().getStatus(), responses.get(0).status());
        assertEquals(trainer.getId(), responses.get(0).trainerId());
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

        assertEquals("404 NOT_FOUND \"Trainee with user name '" + username + "' not found\"", exception.getMessage());

        verify(traineeRepository, only()).findByUsername(username);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Trainer trainer = getTrainer();

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        Optional<TrainerResponse> response = trainerService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(trainer.getUser().getId(), response.get().userId());
        assertEquals(trainer.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainer.getUser().getLastName(), response.get().lastName());
        assertEquals(trainer.getUser().getUsername(), response.get().username());
        assertEquals(trainer.getUser().getStatus(), response.get().status());
        assertEquals(trainer.getId(), response.get().trainerId());

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
        assertEquals(trainer.getUser().getId(), response.get().userId());
        assertEquals(trainer.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainer.getUser().getLastName(), response.get().lastName());
        assertEquals(trainer.getUser().getUsername(), response.get().username());
        assertEquals(trainer.getUser().getStatus(), response.get().status());
        assertEquals(trainer.getId(), response.get().trainerId());

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
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.username());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());

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
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.username());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());

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
                "404 NOT_FOUND \"Trainer with user name '" + username + "' not found\"", exception.getMessage());

        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeStatusById_whenStatusIsEqual() {
        Long id = 1L;
        Trainer trainer = getTrainer();
        var status = UserStatus.ACTIVE;

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.changeStatusById(1L, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testChangeStatusById_whenStatusIsNotEqual() {
        Long id = 1L;
        Trainer trainer = getTrainer();
        var status = UserStatus.SUSPEND;

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.changeStatusById(id, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testChangeStatusByUsername_whenStatusIsEqual() {
        String username = "John.Doe";
        Trainer trainer = getTrainer();
        var status = UserStatus.ACTIVE;

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeStatusByUsername_whenStatusIsNotEqual() {
        String username = "John.Doe";
        Trainer trainer = getTrainer();
        var status = UserStatus.SUSPEND;

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeLoginDataById() {
        Long id = 1L;
        OverrideLoginRequest request = new OverrideLoginRequest("John.Doe", OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        Trainer trainer = getTrainer();

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches(request.oldPassword(), trainer.getUser().getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(request.newPassword())).thenReturn(encodePassword);

        TrainerResponse response = trainerService.changeLoginDataById(id, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.newPassword(), encodePassword));
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.username());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.specialization().specializationType());

        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testChangeLoginDataById_whenPasswordIsNotMatch() {
        Long id = 1L;
        OverrideLoginRequest request
                = new OverrideLoginRequest("John.Doe", OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        Trainer trainer = getTrainer();

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches(request.oldPassword(), trainer.getUser().getPassword()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.newPassword())).thenReturn(encodePassword);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.changeLoginDataById(id, request));

        assertEquals("400 BAD_REQUEST \"Password is incorrect\"", exception.getMessage());

        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testChangeLoginDataById_whenTrainerIsNotFound() {
        Long id = 1L;
        OverrideLoginRequest request = new OverrideLoginRequest("John.Doe", OLD_PASSWORD, NEW_PASSWORD);

        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.changeLoginDataById(id, request));

        assertEquals("404 NOT_FOUND \"Trainer with id '" + id + "' not found\"", exception.getMessage());

        verify(trainerRepository, only()).findById(id);
    }

    @Test
    void testChangeLoginDataByUsername() {
        String username = "John.Doe";
        OverrideLoginRequest request = new OverrideLoginRequest(username, OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        Trainer trainer = getTrainer();

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches(request.oldPassword(), trainer.getUser().getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(request.newPassword())).thenReturn(encodePassword);

        TrainerResponse response = trainerService.changeLoginDataByUsername(username, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.newPassword(), encodePassword));
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.username());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());

        assertEquals(trainer.getSpecialization().getId().ordinal(),
                response.specialization().id());
        assertEquals(trainer.getSpecialization().getSpecialization(),
                response.specialization().specializationType());

        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testChangeLoginDataByUsername_whenPasswordIsNotMatch() {
        String username = "John.Doe";
        OverrideLoginRequest request = new OverrideLoginRequest(username, OLD_PASSWORD, NEW_PASSWORD);
        String encodePassword = controlEncoder.encode(request.newPassword());
        Trainer trainer = getTrainer();

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches(request.oldPassword(), trainer.getUser().getPassword()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.newPassword())).thenReturn(encodePassword);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> trainerService.changeLoginDataByUsername(username, request));

        assertEquals("400 BAD_REQUEST \"Password is incorrect\"", exception.getMessage());

        verify(trainerRepository, only()).findByUsername(username);
    }

    @Test
    void testChangePasswordByUsername_whenTrainerIsNotFound() {
        String username = "John.Doe";
        OverrideLoginRequest request = new OverrideLoginRequest(username, OLD_PASSWORD, NEW_PASSWORD);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.changeLoginDataByUsername(username, request));

        assertEquals("404 NOT_FOUND \"Trainer with user name '" + username + "' not found\"", exception.getMessage());

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
