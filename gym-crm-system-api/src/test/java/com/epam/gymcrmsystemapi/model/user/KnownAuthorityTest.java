package com.epam.gymcrmsystemapi.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KnownAuthorityTest {

    @Test
    void testGetAuthority() {
        KnownAuthority admin = KnownAuthority.ROLE_ADMIN;
        KnownAuthority trainee = KnownAuthority.ROLE_TRAINEE;
        KnownAuthority trainer = KnownAuthority.ROLE_TRAINER;

        assertEquals("ROLE_ADMIN", admin.getAuthority(), "Authority should be 'ROLE_ADMIN'");
        assertEquals("ROLE_TRAINEE", trainee.getAuthority(), "Authority should be 'ROLE_TRAINEE'");
        assertEquals("ROLE_TRAINER", trainer.getAuthority(), "Authority should be 'ROLE_TRAINER'");
    }

    @Test
    void testEnumValues() {
        KnownAuthority[] authorities = KnownAuthority.values();

        assertEquals(3, authorities.length, "There should be 3 authorities");
        assertEquals(KnownAuthority.ROLE_ADMIN, authorities[0]);
        assertEquals(KnownAuthority.ROLE_TRAINEE, authorities[1]);
        assertEquals(KnownAuthority.ROLE_TRAINER, authorities[2]);
    }

    @Test
    void testGetAuthorityReturnsCorrectString() {
        for (KnownAuthority authority : KnownAuthority.values()) {
            assertEquals(authority.name(), authority.getAuthority(),
                    "Authority should match the enum name");
        }
    }
}
