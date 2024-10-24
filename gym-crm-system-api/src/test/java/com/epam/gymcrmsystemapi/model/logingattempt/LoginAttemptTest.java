package com.epam.gymcrmsystemapi.model.logingattempt;

import com.epam.gymcrmsystemapi.model.loginattempt.LoginAttempt;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptTest {

    @Test
    void testConstructorAndGetters() {
        String username = "testUser";
        int attempts = 3;
        OffsetDateTime lastModified = OffsetDateTime.now();

        LoginAttempt loginAttempt = new LoginAttempt(username, attempts, lastModified);

        assertEquals(username, loginAttempt.getUsername());
        assertEquals(attempts, loginAttempt.getAttempts());
        assertEquals(lastModified, loginAttempt.getLastModified());
    }

    @Test
    void testSetters() {
        LoginAttempt loginAttempt = new LoginAttempt();

        loginAttempt.setUsername("newUser");
        loginAttempt.setAttempts(5);
        OffsetDateTime newTime = OffsetDateTime.now();
        loginAttempt.setLastModified(newTime);

        assertEquals("newUser", loginAttempt.getUsername());
        assertEquals(5, loginAttempt.getAttempts());
        assertEquals(newTime, loginAttempt.getLastModified());
    }

    @Test
    void testEqualsAndHashCode() {
        OffsetDateTime now = OffsetDateTime.now();

        LoginAttempt attempt1 = new LoginAttempt("user1", 3, now);
        LoginAttempt attempt2 = new LoginAttempt("user1", 3, now);

        assertEquals(attempt1, attempt2);
        assertEquals(attempt1.hashCode(), attempt2.hashCode());

        LoginAttempt attempt3 = new LoginAttempt("user2", 1, now);
        assertNotEquals(attempt1, attempt3);
        assertNotEquals(attempt1.hashCode(), attempt3.hashCode());
    }
}
