package com.epam.gymcrmsystemapi.service;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
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

class TraineeServiceTest {

    @InjectMocks
    private TraineeService traineeService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Trainee trainee;

    private TraineeSaveMergeRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void testCreateSuccess() {
        String firstName = request.firstName();
        String lastName = request.lastName();

        when(traineeRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(false);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        TraineeResponse response = traineeService.create(request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUsername(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeRepository, times(1)).existsByFirstNameAndLastName(firstName, lastName);
        verify(traineeRepository, times(1)).save(any(Trainee.class));
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testPrivateCalculateUsername() throws Exception {
        String firstName = request.firstName();
        String lastName = request.lastName();

        when(traineeRepository.existsByFirstNameAndLastName(firstName, lastName)).thenReturn(true);
        when(traineeRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(Optional.ofNullable(trainee));

        Method method = TraineeService.class.getDeclaredMethod("calculateUserName", TraineeSaveMergeRequest.class);
        method.setAccessible(true);

        String userName = (String) method.invoke(traineeService, request);

        assertNotNull(userName);
        assertEquals("John.Doe.1", userName);
        verify(traineeRepository, times(1)).existsByFirstNameAndLastName(firstName, lastName);
        verify(traineeRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testPrivateGenerateRandomPassword() throws Exception {
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
    void testList() {
        Page<Trainee> trainingPage = new PageImpl<>(Collections.singletonList(trainee));
        when(traineeRepository.findAll(any(Pageable.class))).thenReturn(trainingPage);

        Page<TraineeResponse> responsePage = traineeService.list(Pageable.unpaged());

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(trainee.getUser().getId(), responsePage.getContent().get(0).userId());
        assertEquals(trainee.getUser().getFirstName(), responsePage.getContent().get(0).firstName());
        assertEquals(trainee.getUser().getLastName(), responsePage.getContent().get(0).lastName());
        assertEquals(trainee.getUser().getUsername(), responsePage.getContent().get(0).userName());
        assertEquals(trainee.getUser().getStatus(), responsePage.getContent().get(0).status());
        assertEquals(trainee.getId(), responsePage.getContent().get(0).traineeId());
        assertEquals(trainee.getDateOfBirth(), responsePage.getContent().get(0).dateOfBirth());
        assertEquals(trainee.getAddress(), responsePage.getContent().get(0).address());
        verify(traineeRepository, only()).findAll(any(Pageable.class));
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testFindById() {
        Long id = 1L;

        when(traineeRepository.findById(id)).thenReturn(Optional.of(trainee));

        Optional<TraineeResponse> response = traineeService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(trainee.getUser().getId(), response.get().userId());
        assertEquals(trainee.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainee.getUser().getLastName(), response.get().lastName());
        assertEquals(trainee.getUser().getUsername(), response.get().userName());
        assertEquals(trainee.getUser().getStatus(), response.get().status());
        assertEquals(trainee.getId(), response.get().traineeId());
        assertEquals(trainee.getDateOfBirth(), response.get().dateOfBirth());
        assertEquals(trainee.getAddress(), response.get().address());
        verify(traineeRepository, only()).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testFindByIdNotFound() {
        Long id = 1L;

        when(traineeRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TraineeResponse> response = traineeService.findById(id);

        assertFalse(response.isPresent());
        verify(traineeRepository, only()).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testFindByUsername() {
        String username = "John.Doe";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        Optional<TraineeResponse> response = traineeService.findByUsername(username);

        assertTrue(response.isPresent());
        assertEquals(trainee.getUser().getId(), response.get().userId());
        assertEquals(trainee.getUser().getFirstName(), response.get().firstName());
        assertEquals(trainee.getUser().getLastName(), response.get().lastName());
        assertEquals(trainee.getUser().getUsername(), response.get().userName());
        assertEquals(trainee.getUser().getStatus(), response.get().status());
        assertEquals(trainee.getId(), response.get().traineeId());
        assertEquals(trainee.getDateOfBirth(), response.get().dateOfBirth());
        assertEquals(trainee.getAddress(), response.get().address());
        verify(traineeRepository, only()).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testFindByUsernameNotFound() {
        String username = "John.Doe";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<TraineeResponse> response = traineeService.findByUsername(username);

        assertFalse(response.isPresent());
        verify(traineeRepository, only()).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testMergeById() {
        Long id = 1L;

        when(traineeRepository.findById(id)).thenReturn(Optional.ofNullable(trainee));

        TraineeResponse response = traineeService.mergeById(id, request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUsername(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeRepository, times(1)).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testMergeByIdTraineeNotFound() {
        Long id = 1L;

        when(traineeRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.mergeById(id, request));

        assertEquals("404 NOT_FOUND \"Trainee with id '1' not found\"", exception.getMessage());

        verify(traineeRepository, times(1)).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testMergeByUsername() {
        String username = "John.Doe";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainee));

        TraineeResponse response = traineeService.mergeByUsername(username, request);

        assertNotNull(response);
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUsername(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testMergeByUsernameNotFound() {
        String username = "John.Doe";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.mergeByUsername(username, request));

        assertEquals("404 NOT_FOUND \"Trainee with user name 'John.Doe' not found\"", exception.getMessage());

        verify(traineeRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangeStatusByIdEqualStatus() {
        Long id = 1L;
        var status = UserStatus.ACTIVE;

        when(traineeRepository.findById(id)).thenReturn(Optional.ofNullable(trainee));

        TraineeResponse response = traineeService.changeStatusById(id, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, times(1)).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangeStatusByIdNotEqualStatus() {
        Long id = 1L;
        var status = UserStatus.SUSPEND;

        when(traineeRepository.findById(id)).thenReturn(Optional.ofNullable(trainee));

        TraineeResponse response = traineeService.changeStatusById(id, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, times(1)).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangeStatusByUsernameEqualStatus() {
        String username = "John.Doe";
        var status = UserStatus.ACTIVE;

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainee));

        TraineeResponse response = traineeService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangeStatusByUsernameNotEqualStatus() {
        String username = "John.Doe";
        var status = UserStatus.SUSPEND;

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainee));

        TraineeResponse response = traineeService.changeStatusByUsername(username, status);

        assertNotNull(response);
        assertEquals(trainee.getUser().getStatus(), response.status());
        verify(traineeRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangePasswordById() {
        Long id = 1L;
        int passwordStrength = 10;
        PasswordEncoder controlEncoder = new BCryptPasswordEncoder(passwordStrength, new SecureRandom());
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");
        String encodePassword = "$2a$10$Y.2j9U6qwbgBtYFgZZi7nu0j96f.CRdSV9pNYw7N.ELH1nv/2905C";

        when(traineeRepository.findById(id)).thenReturn(Optional.ofNullable(trainee));
        when(passwordEncoder.encode(request.password())).thenReturn(encodePassword);

        TraineeResponse response = traineeService.changePasswordById(id, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.password(), encodePassword));
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUsername(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeRepository, times(1)).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangePasswordByIdTraineeNotFound() {
        Long id = 1L;
        var request = new OverridePasswordRequest("aB9dE4fGhJ");

        when(traineeRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.changePasswordById(id, request));

        assertEquals("404 NOT_FOUND \"Trainee with id '1' not found\"", exception.getMessage());

        verify(traineeRepository, times(1)).findById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangePasswordByUsername() {
        String username = "John.Doe";
        int passwordStrength = 10;
        PasswordEncoder controlEncoder = new BCryptPasswordEncoder(passwordStrength, new SecureRandom());
        OverridePasswordRequest request = new OverridePasswordRequest("aB9dE4fGhJ");
        String encodePassword = "$2a$10$Y.2j9U6qwbgBtYFgZZi7nu0j96f.CRdSV9pNYw7N.ELH1nv/2905C";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainee));
        when(passwordEncoder.encode(request.password())).thenReturn(encodePassword);

        TraineeResponse response = traineeService.changePasswordByUsername(username, request);

        assertNotNull(response);
        assertTrue(controlEncoder.matches(request.password(), encodePassword));
        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUsername(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
        verify(traineeRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testChangePasswordByUsernameNotFound() {
        String username = "John.Doe";
        var request = new OverridePasswordRequest("aB9dE4fGhJ");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> traineeService.changePasswordByUsername(username, request));

        assertEquals("404 NOT_FOUND \"Trainee with user name 'John.Doe' not found\"", exception.getMessage());

        verify(traineeRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;

        when(traineeRepository.findById(id)).thenReturn(Optional.ofNullable(trainee));
        doNothing().when(trainingRepository).deleteAllByTrainee(trainee);
        doNothing().when(traineeRepository).deleteById(id);

        traineeService.deleteById(id);

        verify(trainingRepository, times(1)).deleteAllByTrainee(trainee);
        verify(traineeRepository, times(1)).findById(id);
        verify(traineeRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void testDeleteByUsername() {
        String username = "John.Doe";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.ofNullable(trainee));
        doNothing().when(trainingRepository).deleteAllByTrainee(trainee);
        doNothing().when(traineeRepository).deleteByUsername(username);

        traineeService.deleteByUsername(username);

        verify(trainingRepository, times(1)).deleteAllByTrainee(trainee);
        verify(traineeRepository, times(1)).findByUsername(username);
        verify(traineeRepository, times(1)).deleteByUsername(username);
        verifyNoMoreInteractions(traineeRepository);
    }

    private Trainee getTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"));
        trainee.setAddress("123 Main St");
        trainee.setUser(getUser());
        return trainee;
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
