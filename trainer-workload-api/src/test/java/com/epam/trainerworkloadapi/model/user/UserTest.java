package com.epam.trainerworkloadapi.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        User user = new User();

        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setStatus(UserStatus.ACTIVE);

        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        User user1 = new User(1L, "John", "Doe", "johndoe", UserStatus.ACTIVE);
        User user2 = new User(1L, "John", "Doe", "johndoe", UserStatus.ACTIVE);
        User user3 = new User(2L, "Jane", "Smith", "janesmith", UserStatus.SUSPENDED);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);

        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void shouldHaveDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void shouldHaveParameterizedConstructor() {
        User user = new User(1L, "John", "Doe", "johndoe", UserStatus.ACTIVE);

        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }
}
