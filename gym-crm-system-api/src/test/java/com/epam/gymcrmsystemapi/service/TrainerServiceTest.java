package com.epam.gymcrmsystemapi.service;

import com.epam.gymcrmsystemapi.model.trainer.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @InjectMocks
    private TrainerService trainerService;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Trainer trainer;

    private TrainerSaveMergeRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(trainerService, "passwordLength", 10);
        ReflectionTestUtils.setField(trainerService, "passwordCharacters", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()");
        trainer = getTrainer();
        request = new TrainerSaveMergeRequest(
                "John",
                "Doe",
                Specialization.PERSONAL_TRAINER
        );
    }

    @Test
    void testCreateSuccess() {
        String firstName = request.firstName();
        String lastName = request.lastName();

        when(trainerRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(false);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        TrainerResponse response = trainerService.create(request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerRepository, times(1)).existsByFirstNameAndLastName(firstName, lastName);
        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testPrivateCalculateUsername() throws Exception {
        String firstName = request.firstName();
        String lastName = request.lastName();

        when(trainerRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(true);
        when(trainerRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(Optional.ofNullable(trainer));

        Method method = TrainerService.class.getDeclaredMethod("calculateUserName", TrainerSaveMergeRequest.class);
        method.setAccessible(true);

        String userName = (String) method.invoke(trainerService, request);

        assertNotNull(userName);
        assertEquals("John.Doe.1", userName);
        verify(trainerRepository, times(1)).existsByFirstNameAndLastName(firstName, lastName);
        verify(trainerRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testPrivateGenerateRandomPassword() throws Exception {
        int passwordLength = 10;
        String passwordCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

        Method method = TrainerService.class.getDeclaredMethod("generateRandomPassword");
        method.setAccessible(true);

        String password = (String) method.invoke(trainerService);

        assertNotNull(password);
        assertEquals(passwordLength, password.length());
        for (char c : password.toCharArray()) {
            assertTrue(passwordCharacters.contains(String.valueOf(c)));
        }
    }

    @Test
    void testList() {
        Page<Trainer> trainingPage = new PageImpl<>(Collections.singletonList(trainer));
        when(trainerRepository.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TrainerResponse> responsePage = trainerService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainer.getUser().getId(), responsePage.getContent().get(0).userId());
        assertEquals(trainer.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainer.getUser().getUsername(), responsePage.getContent().get(0).userName());
        assertEquals(trainer.getUser().getStatus(), responsePage.getContent().get(0).status());
        assertEquals(trainer.getId(), responsePage.getContent().get(0).trainerId());
        assertEquals(trainer.getSpecialization(), responsePage.getContent().get(0).specialization());
        verify(trainerRepository, only()).findAll(any(Pageable.class));
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testFindById() {
        Long id = 1L;

        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));

        Optional<TrainerResponse> response = trainerService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(trainer.getUser().getId(), response.get().userId());
        assertEquals(trainer.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainer.getUser().getLastName(), response.get().lastName());
        assertEquals(trainer.getUser().getUsername(), response.get().userName());
        assertEquals(trainer.getUser().getStatus(), response.get().status());
        assertEquals(trainer.getId(), response.get().trainerId());
        assertEquals(trainer.getSpecialization(), response.get().specialization());
        verify(trainerRepository, only()).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testFindByIdNotFound() {
        Long id = 1L;

        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TrainerResponse> response = trainerService.findById(id);

        assertFalse(response.isPresent());
        verify(trainerRepository, only()).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testFindByUsername() {
        String username = "John.Doe";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        Optional<TrainerResponse> response = trainerService.findByUsername(username);

        assertTrue(response.isPresent());
        assertEquals(trainer.getUser().getId(), response.get().userId());
        assertEquals(trainer.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainer.getUser().getLastName(), response.get().lastName());
        assertEquals(trainer.getUser().getUsername(), response.get().userName());
        assertEquals(trainer.getUser().getStatus(), response.get().status());
        assertEquals(trainer.getId(), response.get().trainerId());
        assertEquals(trainer.getSpecialization(), response.get().specialization());
        verify(trainerRepository, only()).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testFindByUsernameNotFound() {
        String username = "John.Doe";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<TrainerResponse> response = trainerService.findByUsername(username);

        assertFalse(response.isPresent());
        verify(trainerRepository, only()).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testMergeById() {
        Long id = 1L;

        when(trainerRepository.findById(id)).thenReturn(Optional.ofNullable(trainer));

        TrainerResponse response = trainerService.mergeById(id, request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerRepository, times(1)).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testMergeByIdTraineeNotFound() {
        Long id = 1L;

        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.mergeById(id, request));

        assertEquals("404 NOT_FOUND \"Trainer with id '1' not found\"", exception.getMessage());

        verify(trainerRepository, times(1)).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testMergeByUsername() {
        String username = "John.Doe";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainer));

        TrainerResponse response = trainerService.mergeByUsername(username, request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testMergeByUsernameNotFound() {
        String username = "John.Doe";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.mergeByUsername(username, request));

        assertEquals("404 NOT_FOUND \"Trainer with user name 'John.Doe' not found\"", exception.getMessage());

        verify(trainerRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangeStatusByIdEqualStatus() {
        Long id = 1L;
        var status = UserStatus.ACTIVE;

        when(trainerRepository.findById(id)).thenReturn(Optional.ofNullable(trainer));

        TrainerResponse response = trainerService.changeStatusById(1L, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, times(1)).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangeStatusByIdNotEqualStatus() {
        Long id = 1L;
        var status = UserStatus.SUSPEND;

        when(trainerRepository.findById(id)).thenReturn(Optional.ofNullable(trainer));

        TrainerResponse response = trainerService.changeStatusById(id, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, times(1)).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangeStatusByUsernameEqualStatus() {
        String username = "John.Doe";
        var status = UserStatus.ACTIVE;

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainer));

        TrainerResponse response = trainerService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangeStatusByUsernameNotEqualStatus() {
        String username = "John.Doe";
        var status = UserStatus.SUSPEND;

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainer));

        TrainerResponse response = trainerService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangePasswordById() {
        Long id = 1L;
        int passwordStrength = 10;

        PasswordEncoder controlEncoder = new BCryptPasswordEncoder(passwordStrength, new SecureRandom());
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");
        String encodePassword = "$2a$10$Y.2j9U6qwbgBtYFgZZi7nu0j96f.CRdSV9pNYw7N.ELH1nv/2905C";

        when(trainerRepository.findById(id)).thenReturn(Optional.ofNullable(trainer));
        when(passwordEncoder.encode(request.password())).thenReturn(encodePassword);

        TrainerResponse response = trainerService.changePasswordById(id, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.password(), encodePassword));
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerRepository, times(1)).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangePasswordByIdTrainerNotFound() {
        Long id = 1L;
        var request = new OverridePasswordRequest("aB9dE4fGhJ");

        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.changePasswordById(id, request));

        assertEquals("404 NOT_FOUND \"Trainer with id '1' not found\"", exception.getMessage());

        verify(trainerRepository, times(1)).findById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangePasswordByUsername() {
        String username = "John.Doe";
        int passwordStrength = 10;
        PasswordEncoder controlEncoder = new BCryptPasswordEncoder(passwordStrength, new SecureRandom());
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");
        String encodePassword = "$2a$10$Y.2j9U6qwbgBtYFgZZi7nu0j96f.CRdSV9pNYw7N.ELH1nv/2905C";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainer));
        when(passwordEncoder.encode(request.password())).thenReturn(encodePassword);

        TrainerResponse response = trainerService.changePasswordByUsername(username, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.password(), encodePassword));
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testChangePasswordByUsernameNotFound() {
        String username = "John.Doe";
        var request = new OverridePasswordRequest("aB9dE4fGhJ");

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.changePasswordByUsername(username, request));

        assertEquals("404 NOT_FOUND \"Trainer with user name 'John.Doe' not found\"", exception.getMessage());

        verify(trainerRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;

        doNothing().when(trainerRepository).deleteById(id);

        trainerService.deleteById(id);

        verify(trainerRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void testDeleteByUsername() {
        String username = "John.Doe";

        doNothing().when(trainerRepository).deleteByUsername(username);

        trainerService.deleteByUsername(username);

        verify(trainerRepository, times(1)).deleteByUsername(username);
        verifyNoMoreInteractions(trainerRepository);
    }

    private Trainer getTrainer() {
        var trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setSpecialization(Specialization.PERSONAL_TRAINER);
        trainer1.setUser(getUser());
        return trainer1;
    }

    private User getUser() {
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
