package com.epam.gymcrmsystemapi.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthorityTest {

    private UserAuthority userAuthority;
    private Set<User> users;

    @BeforeEach
    void setUp() {
        userAuthority = new UserAuthority();
        users = new HashSet<>();
    }

    @Test
    void testSetAndGetId() {
        userAuthority.setId(KnownAuthority.ROLE_TRAINEE);
        assertEquals(KnownAuthority.ROLE_TRAINEE, userAuthority.getId());
    }

    @Test
    void testSetAndGetUsers() {
        User user = new User();
        users.add(user);
        userAuthority.setUsers(users);

        assertEquals(1, userAuthority.getUsers().size());
        assertTrue(userAuthority.getUsers().contains(user));
    }

    @Test
    void testEqualsAndHashCode() {
        UserAuthority authority1 = new UserAuthority();
        authority1.setId(KnownAuthority.ROLE_TRAINEE);

        UserAuthority authority2 = new UserAuthority();
        authority2.setId(KnownAuthority.ROLE_TRAINEE);

        assertEquals(authority1, authority2);
        assertEquals(authority1.hashCode(), authority2.hashCode());

        authority2.setId(KnownAuthority.ROLE_ADMIN);
        assertNotEquals(authority1, authority2);
        assertNotEquals(authority1.hashCode(), authority2.hashCode());
    }
}
