package com.epam.gymcrmsystemapi.service;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.trainer.TrainerDAO;
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

public class TrainerServiceTest {

    @InjectMocks
    private TrainerService trainerService;

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TrainerSaveMergeRequest request;

    private Trainer trainer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        trainerService.setPasswordEncoder(passwordEncoder);
        ReflectionTestUtils.setField(trainerService, "passwordLength", 10);
        ReflectionTestUtils.setField(trainerService, "passwordCharacters", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()");
        trainer = getTrainer();
        request = new TrainerSaveMergeRequest(
                "John",
                "Doe",
                "Box"
        );
    }

    @Test
    public void testCreateSuccess() {
        when(trainerDAO.save(any(Trainer.class))).thenReturn(trainer);

        TrainerResponse response = trainerService.create(request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUserName(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerDAO, times(1)).existByFirstNameAndLastName(any(String.class), any(String.class));
        verify(trainerDAO, times(1)).save(any(Trainer.class));
    }

    @Test
    public void testPrivateGetUser() throws Exception {
        when(trainerDAO.existByFirstNameAndLastName(request.firstName(), request.lastName())).thenReturn(true);
        when(trainerDAO.findByFirstNameAndLastName(request.firstName(), request.lastName())).thenReturn(Optional.ofNullable(trainer));

        Method method = TrainerService.class.getDeclaredMethod("calculateUserName", TrainerSaveMergeRequest.class);
        method.setAccessible(true);

        String userName = (String) method.invoke(trainerService, request);

        assertNotNull(userName);
        assertEquals("John.Doe.1", userName);
    }

    @Test
    public void testPrivateGenerateRandomPassword() throws Exception {
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
    public void testList() {
        Page<Trainer> trainingPage = new PageImpl<>(Collections.singletonList(trainer));
        when(trainerDAO.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TrainerResponse> responsePage = trainerService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainer.getUser().getId(), responsePage.getContent().get(0).userId());
        assertEquals(trainer.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainer.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainer.getUser().getUserName(), responsePage.getContent().get(0).userName());
        assertEquals(trainer.getUser().getStatus(), responsePage.getContent().get(0).status());
        assertEquals(trainer.getId(), responsePage.getContent().get(0).trainerId());
        assertEquals(trainer.getSpecialization(), responsePage.getContent().get(0).specialization());
        verify(trainerDAO, only()).findAll(any(Pageable.class));
    }

    @Test
    void testFindById() {
        when(trainerDAO.findById(1L)).thenReturn(Optional.of(trainer));

        Optional<TrainerResponse> response = trainerService.findById(1L);

        assertTrue(response.isPresent());
        assertEquals(trainer.getUser().getId(), response.get().userId());
        assertEquals(trainer.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainer.getUser().getLastName(), response.get().lastName());
        assertEquals(trainer.getUser().getUserName(), response.get().userName());
        assertEquals(trainer.getUser().getStatus(), response.get().status());
        assertEquals(trainer.getId(), response.get().trainerId());
        assertEquals(trainer.getSpecialization(), response.get().specialization());
        verify(trainerDAO, only()).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(trainerDAO.findById(1L)).thenReturn(Optional.empty());

        Optional<TrainerResponse> response = trainerService.findById(1L);

        assertFalse(response.isPresent());
        verify(trainerDAO, only()).findById(1L);
    }

    @Test
    public void testMergeById() {
        when(trainerDAO.changeById(1L, trainer)).thenReturn(trainer);
        when(trainerDAO.findById(1L)).thenReturn(Optional.ofNullable(trainer));

        TrainerResponse response = trainerService.mergeById(1L, request);

        assertNotNull(response);
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUserName(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerDAO, times(1)).findById(1L);
        verify(trainerDAO, times(1)).changeById(1L, trainer);
    }

    @Test
    public void testMergeByIdTraineeNotFound() {
        when(trainerDAO.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.mergeById(1L, request));

        assertEquals("404 NOT_FOUND \"Trainer with id '1' not found\"", exception.getMessage());

        verify(trainerDAO, times(1)).findById(anyLong());
        verify(trainerDAO, times(0)).changeById(1L, trainer);
    }

    @Test
    public void testChangeStatusByIdEqualStatus() {
        var status = UserStatus.ACTIVE;

        when(trainerDAO.findById(1L)).thenReturn(Optional.ofNullable(trainer));
        when(trainerDAO.changeById(1L, trainer)).thenReturn(trainer);

        TrainerResponse response = trainerService.changeStatusById(1L, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerDAO, times(1)).findById(1L);
        verify(trainerDAO, times(0)).changeById(1L, trainer);
    }

    @Test
    public void testChangeStatusByIdNotEqualStatus() {
        var status = UserStatus.SUSPEND;

        when(trainerDAO.findById(1L)).thenReturn(Optional.ofNullable(trainer));
        when(trainerDAO.changeById(1L, trainer)).thenReturn(trainer);

        TrainerResponse response = trainerService.changeStatusById(1L, status);

        assertNotNull(response);
        assertEquals(trainer.getUser().getStatus(), response.status());
        verify(trainerDAO, times(1)).findById(1L);
        verify(trainerDAO, times(1)).changeById(1L, trainer);
    }

    @Test
    public void testChangePasswordById() {
        PasswordEncoder controlEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");
        String encodePassword = "$2a$10$Y.2j9U6qwbgBtYFgZZi7nu0j96f.CRdSV9pNYw7N.ELH1nv/2905C";

        when(trainerDAO.findById(1L)).thenReturn(Optional.ofNullable(trainer));
        when(passwordEncoder.encode(request.password())).thenReturn(encodePassword);
        when(trainerDAO.changeById(1L, trainer)).thenReturn(trainer);

        TrainerResponse response = trainerService.changePasswordById(1L, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.password(), encodePassword));
        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUserName(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
        verify(trainerDAO, times(1)).findById(1L);
        verify(trainerDAO, times(1)).changeById(1L, trainer);
    }

    @Test
    public void testChangePasswordByIdTrainerNotFound() {
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");

        when(trainerDAO.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> trainerService.changePasswordById(1L, request));

        assertEquals("404 NOT_FOUND \"Trainer with id '1' not found\"", exception.getMessage());

        verify(trainerDAO, times(1)).findById(anyLong());
        verify(trainerDAO, times(0)).changeById(anyLong(), any(Trainer.class));
    }

    @Test
    public void testDelete() {
        doNothing().when(trainerDAO).deleteById(1L);

        trainerService.deleteById(1L);

        verify(trainerDAO, only()).deleteById(1L);
    }

    private Trainer getTrainer() {
        var trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setSpecialization("Box");
        trainer1.setUser(getUser());
        return trainer1;
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
