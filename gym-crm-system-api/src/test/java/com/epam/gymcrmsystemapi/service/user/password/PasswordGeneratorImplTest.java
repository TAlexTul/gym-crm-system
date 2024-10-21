package com.epam.gymcrmsystemapi.service.user.password;

import com.epam.gymcrmsystemapi.service.user.password.PasswordGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorImplTest {

    @InjectMocks
    private PasswordGeneratorImpl passwordGenerator;

    private final static int PASSWORD_LENGTH = 10;
    private final static String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(passwordGenerator, "passwordLength", PASSWORD_LENGTH);
        ReflectionTestUtils.setField(passwordGenerator, "passwordCharacters", PASSWORD_CHARACTERS);
    }

    @Test
    void testGenerateRandomPassword() {
        String password = passwordGenerator.generateRandomPassword();

        assertNotNull(password);
        assertEquals(PASSWORD_LENGTH, password.length());
        for (char c : password.toCharArray()) {
            assertTrue(PASSWORD_CHARACTERS.contains(String.valueOf(c)));
        }
    }
}
