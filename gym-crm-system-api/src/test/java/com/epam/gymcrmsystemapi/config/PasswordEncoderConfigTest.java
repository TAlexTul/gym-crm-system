package com.epam.gymcrmsystemapi.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderConfigTest {

    private PasswordEncoderConfig passwordEncoderConfig;

    @BeforeEach
    void setUp() {
        passwordEncoderConfig = new PasswordEncoderConfig();
        ReflectionTestUtils.setField(passwordEncoderConfig, "strength", 10);
    }

    @Test
    void testPasswordEncoderStrength() {
        PasswordEncoder passwordEncoder = passwordEncoderConfig.passwordEncoder();

        assertNotNull(passwordEncoder);

        String encodedPassword = passwordEncoder.encode("aB9dE4fGhJ");
        assertTrue(passwordEncoder.matches("aB9dE4fGhJ", encodedPassword));
    }
}
