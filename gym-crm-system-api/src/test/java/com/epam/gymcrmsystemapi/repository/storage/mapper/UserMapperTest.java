package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void testMap() {
        String[] data = {
                "1",
                "John",
                "Doe",
                "John.Doe",
                "aB9dE4fGhJ",
                "ACTIVE"
        };

        User result = userMapper.map(data);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("John.Doe", result.getUserName());
        assertEquals("aB9dE4fGhJ", result.getPassword());
        assertEquals(UserStatus.ACTIVE, result.getStatus());
    }
}
