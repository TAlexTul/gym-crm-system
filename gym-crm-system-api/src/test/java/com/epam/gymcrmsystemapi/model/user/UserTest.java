package com.epam.gymcrmsystemapi.model.user;

import org.junit.jupiter.api.Test;

import static com.epam.gymcrmsystemapi.Data.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    @Test
    void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(ID_USER_1, getUser1().getId());
        assertEquals(FIRST_NAME_1, getUser1().getFirstName());
        assertEquals(LAST_NAME_1, getUser1().getLastName());
        assertEquals(USERNAME_1, getUser1().getUsername());
        assertEquals(PASSWORD_1, getUser1().getPassword());
        assertEquals(UserStatus.ACTIVE, getUser1().getStatus());
    }

    @Test
    void testEquals() {
        assertEquals(getUser1(), getUser1());
    }

    @Test
    void testNotEquals() {
        assertNotEquals(getUser1(), getUser2());
    }

    @Test
    void testHashCodeMatch() {
        assertEquals(getUser1().hashCode(), getUser1().hashCode());
    }

    @Test
    void testHashCodeNotMatch() {
        assertNotEquals(getUser1().hashCode(), getUser2().hashCode());
    }

    private User getUser1() {
        return new User(
                ID_USER_1,
                FIRST_NAME_1,
                LAST_NAME_1,
                USERNAME_1,
                PASSWORD_1,
                UserStatus.ACTIVE
        );
    }

    private User getUser2() {
        return new User(
                ID_USER_2,
                FIRST_NAME_2,
                LAST_NAME_2,
                USERNAME_2,
                PASSWORD_2,
                UserStatus.ACTIVE
        );
    }
}
