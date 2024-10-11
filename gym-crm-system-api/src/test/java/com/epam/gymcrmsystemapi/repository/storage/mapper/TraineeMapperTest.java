package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class TraineeMapperTest {

    private TraineeMapper traineeMapper;

    @Mock
    private DataMapper<User> userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeMapper = new TraineeMapper();
        traineeMapper.setMapper(userMapper);
    }

    @Test
    void testMap() {
        String[] data = {
                "1",
                "John",
                "Doe",
                "john.doe",
                "aB9dE4fGhJ",
                "ACTIVE",
                "123",
                "2024-10-06T10:15:30+01:00",
                "123 Main St"
        };

        User user = getUser();
        when(userMapper.map(data)).thenReturn(user);

        Trainee result = traineeMapper.map(data);

        assertNotNull(result);
        assertEquals(123L, result.getId());
        assertEquals(OffsetDateTime.parse("2024-10-06T10:15:30+01:00"), result.getDateOfBirth());
        assertEquals("123 Main St", result.getAddress());
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
