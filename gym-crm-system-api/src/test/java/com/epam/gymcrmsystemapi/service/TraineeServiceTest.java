package com.epam.gymcrmsystemapi.service;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.trainee.TraineeDAO;
import com.epam.gymcrmsystemapi.service.trainee.TraineeService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TraineeServiceTest {

    @InjectMocks
    private TraineeService traineeService;

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Trainee trainee;

    private TraineeSaveMergeRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeService.setPasswordEncoder(passwordEncoder);
        ReflectionTestUtils.setField(traineeService, "passwordLength", 10);
        ReflectionTestUtils.setField(traineeService, "passwordCharacters", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()");
        trainee = getTrainee();
        request = new TraineeSaveMergeRequest(
                "John",
                "Doe",
                OffsetDateTime.parse("2007-12-03T10:15:30+01:00"),
                "123 Main St"
        );
    }

    @Test
    public void testCreateSuccess() {
        when(traineeDAO.save(any(Trainee.class))).thenReturn(trainee);

        TraineeResponse response = traineeService.create(request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUserName(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeDAO, times(1)).existByFirstNameAndLastName(any(String.class), any(String.class));
        verify(traineeDAO, times(1)).save(any(Trainee.class));
    }

    @Test
    public void testPrivateGetUser() throws Exception {
        when(traineeDAO.existByFirstNameAndLastName(request.firstName(), request.lastName())).thenReturn(true);
        when(traineeDAO.findByFirstNameAndLastName(request.firstName(), request.lastName())).thenReturn(Optional.ofNullable(trainee));

        Method method = TraineeService.class.getDeclaredMethod("calculateUserName", TraineeSaveMergeRequest.class);
        method.setAccessible(true);

        String userName = (String) method.invoke(traineeService, request);

        assertNotNull(userName);
        assertEquals("John.Doe.1", userName);
    }

    @Test
    public void testPrivateGenerateRandomPassword() throws Exception {
        int passwordLength = 10;
        String passwordCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

        Method method = TraineeService.class.getDeclaredMethod("generateRandomPassword");
        method.setAccessible(true);

        String password = (String) method.invoke(traineeService);

        assertNotNull(password);
        assertEquals(passwordLength, password.length());
        for (char c : password.toCharArray()) {
            assertTrue(passwordCharacters.contains(String.valueOf(c)));
        }
    }

    @Test
    public void testList() {
        Page<Trainee> trainingPage = new PageImpl<>(Collections.singletonList(trainee));
        when(traineeDAO.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TraineeResponse> responsePage = traineeService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainee.getUser().getId(), responsePage.getContent().get(0).userId());
        assertEquals(trainee.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainee.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainee.getUser().getUserName(), responsePage.getContent().get(0).userName());
        assertEquals(trainee.getUser().getStatus(), responsePage.getContent().get(0).status());
        assertEquals(trainee.getId(), responsePage.getContent().get(0).traineeId());
        assertEquals(trainee.getDateOfBirth(), responsePage.getContent().get(0).dateOfBirth());
        assertEquals(trainee.getAddress(), responsePage.getContent().get(0).address());
        verify(traineeDAO, only()).findAll(any(Pageable.class));
    }

    @Test
    void testFindById() {
        when(traineeDAO.findById(1L)).thenReturn(Optional.of(trainee));

        Optional<TraineeResponse> response = traineeService.findById(1L);

        assertTrue(response.isPresent());
        assertEquals(trainee.getUser().getId(), response.get().userId());
        assertEquals(trainee.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainee.getUser().getLastName(), response.get().lastName());
        assertEquals(trainee.getUser().getUserName(), response.get().userName());
        assertEquals(trainee.getUser().getStatus(), response.get().status());
        assertEquals(trainee.getId(), response.get().traineeId());
        assertEquals(trainee.getDateOfBirth(), response.get().dateOfBirth());
        assertEquals(trainee.getAddress(), response.get().address());
        verify(traineeDAO, only()).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(traineeDAO.findById(1L)).thenReturn(Optional.empty());

        Optional<TraineeResponse> response = traineeService.findById(1L);

        assertFalse(response.isPresent());
        verify(traineeDAO, only()).findById(1L);
    }

    @Test
    public void testMergeById() {
        when(traineeDAO.mergeById(any(Long.class), any(Trainee.class))).thenReturn(trainee);
        when(traineeDAO.findById(1L)).thenReturn(Optional.ofNullable(trainee));

        TraineeResponse response = traineeService.mergeById(1L, request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUserName(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeDAO, times(1)).findById(1L);
        verify(traineeDAO, times(1)).mergeById(1L, trainee);
    }

    @Test
    public void testMergeByIdTraineeNotFound() {
        when(traineeDAO.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.mergeById(1L, request));

        assertEquals("404 NOT_FOUND \"Trainee with id '1' not found\"", exception.getMessage());

        verify(traineeDAO, times(1)).findById(anyLong());
        verify(traineeDAO, times(0)).mergeById(1L, trainee);
    }

    @Test
    public void testChangeStatusByIdEqualStatus() {
        var status = UserStatus.ACTIVE;

        when(traineeDAO.findById(1L)).thenReturn(Optional.ofNullable(trainee));
        when(traineeDAO.changeStatusById(1L, trainee)).thenReturn(trainee);

        TraineeResponse response = traineeService.changeStatusById(1L, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeDAO, times(1)).findById(1L);
        verify(traineeDAO, times(0)).changeStatusById(1L, trainee);
    }

    @Test
    public void testChangeStatusByIdNotEqualStatus() {
        var status = UserStatus.SUSPEND;

        when(traineeDAO.findById(1L)).thenReturn(Optional.ofNullable(trainee));
        when(traineeDAO.changeStatusById(1L, trainee)).thenReturn(trainee);

        TraineeResponse response = traineeService.changeStatusById(1L, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeDAO, times(1)).findById(1L);
        verify(traineeDAO, times(1)).changeStatusById(1L, trainee);
    }

    @Test
    public void testChangePasswordById() {
        PasswordEncoder controlEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");
        String encodePassword = "$2a$10$Y.2j9U6qwbgBtYFgZZi7nu0j96f.CRdSV9pNYw7N.ELH1nv/2905C";

        when(traineeDAO.findById(1L)).thenReturn(Optional.ofNullable(trainee));
        when(passwordEncoder.encode(request.password())).thenReturn(encodePassword);
        when(traineeDAO.changePasswordById(1L, trainee)).thenReturn(trainee);

        TraineeResponse response = traineeService.changePasswordById(1L, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.password(), encodePassword));
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUserName(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeDAO, times(1)).findById(1L);
        verify(traineeDAO, times(1)).changePasswordById(1L, trainee);
    }

    @Test
    public void testChangePasswordByIdTraineeNotFound() {
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");

        when(traineeDAO.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.changePasswordById(1L, request));

        assertEquals("404 NOT_FOUND \"Trainee with id '1' not found\"", exception.getMessage());

        verify(traineeDAO, times(1)).findById(anyLong());
        verify(traineeDAO, times(0)).changePasswordById(anyLong(), any(Trainee.class));
    }

    @Test
    public void testDelete() {
        doNothing().when(traineeDAO).deleteById(1L);

        traineeService.deleteById(1L);

        verify(traineeDAO, only()).deleteById(1L);
    }

    private Trainee getTrainee() {
        var trainee1 = new Trainee();
        trainee1.setId(1L);
        trainee1.setDateOfBirth(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"));
        trainee1.setAddress("123 Main St");
        trainee1.setUser(getUser());
        return trainee1;
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
