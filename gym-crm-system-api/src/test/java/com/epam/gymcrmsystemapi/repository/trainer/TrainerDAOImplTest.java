package com.epam.gymcrmsystemapi.repository.trainer;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainerDAOImplTest {

    @InjectMocks
    private TrainerDAOImpl trainerDAO;

    @Mock
    private TrainerStorage trainerStorage;

    @Mock
    private Map<Long, Trainer> trainerMap;

    private Trainer trainer1;

    private Trainer trainer2;

    private Trainer trainer3;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(trainerStorage.getTrainerStorage()).thenReturn(trainerMap);
        trainer1 = getTrainer1();
        trainer2 = getTrainer2();
        trainer3 = getTrainer3();
    }

    @Test
    public void testSave() {
        trainer1.getUser().setId(null);
        trainer1.setId(null);

        when(trainerMap.values()).thenReturn(new ArrayList<>());
        when(trainerMap.keySet()).thenReturn(new TreeSet<>());
        when(trainerMap.put(any(Long.class), any(Trainer.class))).thenReturn(trainer1);

        Trainer entity = trainerDAO.save(trainer1);

        assertEquals(1L, entity.getId());
        assertEquals(1L, entity.getUser().getId());
        verify(trainerMap, times(1)).values();
        verify(trainerMap, times(1)).keySet();
        verify(trainerMap, times(1)).put(1L, this.trainer1);
    }

    @Test
    public void testSave_StorageNotEmpty() {
        Map<Long, Trainer> trainers = new TreeMap<>();
        trainers.put(trainer2.getId(), trainer2);
        trainers.put(trainer3.getId(), trainer3);
        trainer1.getUser().setId(null);
        trainer1.setId(null);

        when(trainerMap.values()).thenReturn(trainers.values());
        when(trainerMap.keySet()).thenReturn(trainers.keySet());
        when(trainerMap.put(any(Long.class), any(Trainer.class))).thenReturn(trainer1);

        Trainer entity = trainerDAO.save(trainer1);

        assertEquals(4L, entity.getId());
        assertEquals(4L, entity.getUser().getId());
        verify(trainerMap, times(1)).values();
        verify(trainerMap, times(1)).keySet();
        verify(trainerMap, times(1)).put(4L, trainer1);
    }

    @Test
    public void testList() {
        Map<Long, Trainer> trainers = new TreeMap<>();
        trainers.put(trainer1.getId(), trainer1);
        trainers.put(trainer2.getId(), trainer2);
        trainers.put(trainer3.getId(), trainer3);

        int pageNumber = 0;
        int pageSize = 2;
        PageRequest trainerPage = PageRequest.of(pageNumber, pageSize);

        when(trainerMap.values()).thenReturn(trainers.values());

        Page<Trainer> responsePage = trainerDAO.findAll(trainerPage);

        assertNotNull(responsePage);
        assertEquals(3, responsePage.getTotalElements());
        assertEquals(0, responsePage.getNumber());
        assertEquals(2, responsePage.getTotalPages());
        assertEquals(2, responsePage.getContent().size());
        verify(trainerMap, times(1)).values();
    }

    @Test
    public void testFindById() {
        when(trainerMap.get(1L)).thenReturn(trainer1);

        Optional<Trainer> response = trainerDAO.findById(1L);

        assertTrue(response.isPresent());
        assertEquals(trainer1.getUser().getId(), response.get().getUser().getId());
        assertEquals(trainer1.getUser().getFirstName(), response.get().getUser().getFirstName());
        assertEquals(trainer1.getUser().getLastName(), response.get().getUser().getLastName());
        assertEquals(trainer1.getUser().getUserName(), response.get().getUser().getUserName());
        assertEquals(trainer1.getUser().getStatus(), response.get().getUser().getStatus());
        assertEquals(trainer1.getId(), response.get().getId());
        assertEquals(trainer1.getSpecialization(), response.get().getSpecialization());
        verify(trainerMap, only()).get(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(trainerMap.get(1L)).thenReturn(null);

        Optional<Trainer> response = trainerDAO.findById(1L);

        assertFalse(response.isPresent());
        verify(trainerMap, only()).get(1L);
    }

    @Test
    public void testExistByFirstNameAndLastName() {
        String firstName = trainer1.getUser().getFirstName();
        String lastName = trainer1.getUser().getLastName();

        when(trainerMap.values()).thenReturn(List.of(trainer1));

        boolean isExist = trainerDAO.existByFirstNameAndLastName(firstName, lastName);

        assertTrue(isExist);
        verify(trainerMap, only()).values();
    }

    @Test
    public void testExistByFirstNameAndLastName_TrainerNotExist() {
        String firstName = trainer1.getUser().getFirstName();
        String lastName = trainer1.getUser().getLastName();

        when(trainerMap.values()).thenReturn(new ArrayList<>());

        boolean isExist = trainerDAO.existByFirstNameAndLastName(firstName, lastName);

        assertFalse(isExist);
        verify(trainerMap, only()).values();
    }

    @Test
    public void testMergeById() {
        trainerDAO.mergeById(1L, trainer1);

        verify(trainerMap, only()).put(1L, trainer1);
    }

    @Test
    public void testChangeStatusById() {
        trainerDAO.changeStatusById(1L, trainer1);

        verify(trainerMap, only()).put(1L, trainer1);
    }

    @Test
    public void testChangePasswordById() {
        trainerDAO.changePasswordById(1L, trainer1);

        verify(trainerMap, only()).put(1L, trainer1);
    }


    @Test
    public void deleteById() {
        trainerDAO.deleteById(1L);

        verify(trainerMap, only()).remove(1L);
    }

    private Trainer getTrainer1() {
        var trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setSpecialization("Box");
        trainer1.setUser(getUser1());
        return trainer1;
    }

    private Trainer getTrainer2() {
        var trainer1 = new Trainer();
        trainer1.setId(2L);
        trainer1.setSpecialization("Football");
        trainer1.setUser(getUser2());
        return trainer1;
    }

    private Trainer getTrainer3() {
        var trainer2 = new Trainer();
        trainer2.setId(3L);
        trainer2.setSpecialization("Volleyball");
        trainer2.setUser(getUser3());
        return trainer2;
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
