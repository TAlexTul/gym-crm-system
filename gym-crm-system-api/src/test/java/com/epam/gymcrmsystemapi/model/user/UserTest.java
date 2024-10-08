package com.epam.gymcrmsystemapi.model.user;

import com.epam.gymcrmsystemapi.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTest {

    @Test
    public void testConstructorWithoutArgsWithSettersAndGetters() {
        assertEquals(Data.ID_USER_1, getUser1().getId());
        assertEquals(Data.FIRST_NAME_1, getUser1().getFirstName());
        assertEquals(Data.LAST_NAME_1, getUser1().getLastName());
        assertEquals(Data.USER_NAME_1, getUser1().getUserName());
        assertEquals(Data.PASSWORD_1, getUser1().getPassword());
        assertEquals(UserStatus.ACTIVE, getUser1().getStatus());
    }

    @Test
    public void testEquals() {
        assertEquals(getUser1(), getUser1());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(getUser1(), getUser2());
    }

    @Test
    public void testHashCodeMatch() {
        assertEquals(getUser1().hashCode(), getUser1().hashCode());
    }

    @Test
    public void testHashCodeNotMatch() {
        assertNotEquals(getUser1().hashCode(), getUser2().hashCode());
    }

    private User getUser1() {
        var user = new User();
        user.setId(Data.ID_USER_1);
        user.setFirstName(Data.FIRST_NAME_1);
        user.setLastName(Data.LAST_NAME_1);
        user.setUserName(Data.USER_NAME_1);
        user.setPassword(Data.PASSWORD_1);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUser2() {
        var user = new User();
        user.setId(Data.ID_USER_2);
        user.setFirstName(Data.FIRST_NAME_2);
        user.setLastName(Data.LAST_NAME_2);
        user.setUserName(Data.USER_NAME_2);
        user.setPassword(Data.PASSWORD_2);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
