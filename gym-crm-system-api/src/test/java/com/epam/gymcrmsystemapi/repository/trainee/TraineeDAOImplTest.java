package com.epam.gymcrmsystemapi.repository.trainee;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TraineeDAOImplTest {

    @InjectMocks
    private TraineeDAOImpl traineeDAO;

    @Mock
    private TraineeStorage traineeStorage;

    @Mock
    private Map<Long, Trainee> traineeMap;

    private Trainee trainee1;

    private Trainee trainee3;

    private Trainee trainee2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(traineeStorage.getTraineeStorage()).thenReturn(traineeMap);
        trainee1 = getTrainee1();
        trainee2 = getTrainee2();
        trainee3 = getTrainee3();
    }

    @Test
    public void testSave() {
        trainee1.getUser().setId(null);
        trainee1.setId(null);

        when(traineeMap.values()).thenReturn(new ArrayList<>());
        when(traineeMap.keySet()).thenReturn(new TreeSet<>());
        when(traineeMap.put(any(Long.class), any(Trainee.class))).thenReturn(trainee1);

        Trainee entity = traineeDAO.save(trainee1);

        assertEquals(1L, entity.getId());
        assertEquals(1L, entity.getUser().getId());
        verify(traineeMap, times(1)).values();
        verify(traineeMap, times(1)).keySet();
        verify(traineeMap, times(1)).put(1L, trainee1);
    }

    @Test
    public void testSave_StorageNotEmpty() {
        Map<Long, Trainee> trainees = new TreeMap<>();
        trainees.put(trainee3.getId(), trainee3);
        trainees.put(trainee2.getId(), trainee2);
        trainee1.getUser().setId(null);
        trainee1.setId(null);

        when(traineeMap.values()).thenReturn(trainees.values());
        when(traineeMap.keySet()).thenReturn(trainees.keySet());
        when(traineeMap.put(any(Long.class), any(Trainee.class))).thenReturn(trainee1);

        Trainee entity = traineeDAO.save(trainee1);

        assertEquals(4L, entity.getId());
        assertEquals(4L, entity.getUser().getId());
        verify(traineeMap, times(1)).values();
        verify(traineeMap, times(1)).keySet();
        verify(traineeMap, times(1)).put(4L, trainee1);
    }

    @Test
    public void testList() {
        Map<Long, Trainee> trainees = new TreeMap<>();
        trainees.put(trainee1.getId(), trainee1);
        trainees.put(trainee3.getId(), trainee3);
        trainees.put(trainee2.getId(), trainee2);

        int pageNumber = 0;
        int pageSize = 2;
        PageRequest traineePage = PageRequest.of(pageNumber, pageSize);

        when(traineeMap.values()).thenReturn(trainees.values());

        Page<Trainee> responsePage = traineeDAO.findAll(traineePage);

        assertNotNull(responsePage);
        assertEquals(3, responsePage.getTotalElements());
        assertEquals(0, responsePage.getNumber());
        assertEquals(2, responsePage.getTotalPages());
        assertEquals(2, responsePage.getContent().size());
        verify(traineeMap, times(1)).values();
    }

    @Test
    public void testFindById() {
        when(traineeMap.get(1L)).thenReturn(trainee1);

        Optional<Trainee> response = traineeDAO.findById(1L);

        assertTrue(response.isPresent());
        assertEquals(trainee1.getUser().getId(), response.get().getUser().getId());
        assertEquals(trainee1.getUser().getFirstName(), response.get().getUser().getFirstName());
        assertEquals(trainee1.getUser().getLastName(), response.get().getUser().getLastName());
        assertEquals(trainee1.getUser().getUserName(), response.get().getUser().getUserName());
        assertEquals(trainee1.getUser().getStatus(), response.get().getUser().getStatus());
        assertEquals(trainee1.getId(), response.get().getId());
        assertEquals(trainee1.getDateOfBirth(), response.get().getDateOfBirth());
        assertEquals(trainee1.getAddress(), response.get().getAddress());
        verify(traineeMap, only()).get(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(traineeMap.get(1L)).thenReturn(null);

        Optional<Trainee> response = traineeDAO.findById(1L);

        assertFalse(response.isPresent());
        verify(traineeMap, only()).get(1L);
    }

    @Test
    public void testExistByFirstNameAndLastName() {
        String firstName = trainee1.getUser().getFirstName();
        String lastName = trainee1.getUser().getLastName();

        when(traineeMap.values()).thenReturn(List.of(trainee1));

        boolean isExist = traineeDAO.existByFirstNameAndLastName(firstName, lastName);

        assertTrue(isExist);
        verify(traineeMap, only()).values();
    }

    @Test
    public void testExistByFirstNameAndLastName_TraineeNotExist() {
        String firstName = trainee1.getUser().getFirstName();
        String lastName = trainee1.getUser().getLastName();

        when(traineeMap.values()).thenReturn(new ArrayList<>());

        boolean isExist = traineeDAO.existByFirstNameAndLastName(firstName, lastName);

        assertFalse(isExist);
        verify(traineeMap, only()).values();
    }

    @Test
    public void testChange() {
        traineeDAO.changeById(1L, trainee1);

        verify(traineeMap, only()).put(1L, trainee1);
    }

    @Test
    public void deleteById() {
        traineeDAO.deleteById(1L);

        verify(traineeMap, only()).remove(1L);
    }

    private Trainee getTrainee1() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.now());
        trainee.setAddress("123 Main St");
        trainee.setUser(getUser1());
        return trainee;
    }

    private Trainee getTrainee2() {
        var trainee = new Trainee();
        trainee.setId(2L);
        trainee.setDateOfBirth(OffsetDateTime.now().plusDays(2L));
        trainee.setAddress("101 Independence Avenue");
        trainee.setUser(getUser2());
        return trainee;
    }

    private Trainee getTrainee3() {
        var trainee = new Trainee();
        trainee.setId(3L);
        trainee.setDateOfBirth(OffsetDateTime.now().plusDays(1L));
        trainee.setAddress("4 Brain St");
        trainee.setUser(getUser3());
        return trainee;
    }

    private User getUser1() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUser2() {
        var user = new User();
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUserName("Jane.Smith");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUser3() {
        var user = new User();
        user.setId(3L);
        user.setFirstName("Emily");
        user.setLastName("Johnson");
        user.setUserName("Emily.Johnson");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
