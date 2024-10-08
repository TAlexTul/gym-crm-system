package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TrainerMapperTest {

    private TrainerMapper trainerMapper;

    @Mock
    private DataMapper<User> userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainerMapper = new TrainerMapper();
        trainerMapper.setMapper(userMapper);
    }

    @Test
    void testMap() {
        String[] data = {
                "1", "Jane", "Doe", "jane.doe", "aB9dE4fGhJ", "ACTIVE", "456", "Fitness Instructor"
        };

        User user = getUser();
        when(userMapper.map(data)).thenReturn(user);

        Trainer result = trainerMapper.map(data);

        assertNotNull(result);
        assertEquals(456L, result.getId());
        assertEquals("Fitness Instructor", result.getSpecialization());
        assertNotNull(result.getUser());
        assertEquals(user, result.getUser());
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
