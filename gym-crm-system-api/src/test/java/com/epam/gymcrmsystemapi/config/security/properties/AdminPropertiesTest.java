package com.epam.gymcrmsystemapi.config.security.properties;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminPropertiesTest {

    private final Validator validator;

    public AdminPropertiesTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSetAndGetFirstName() {
        AdminProperties adminProperties = new AdminProperties();
        String expectedFirstName = "John";

        adminProperties.setFirstName(expectedFirstName);

        assertThat(adminProperties.getFirstName()).isEqualTo(expectedFirstName);
    }

    @Test
    void testSetAndGetLastName() {
        AdminProperties adminProperties = new AdminProperties();
        String expectedLastName = "Doe";

        adminProperties.setLastName(expectedLastName);

        assertThat(adminProperties.getLastName()).isEqualTo(expectedLastName);
    }

    @Test
    void testSetAndGetPassword() {
        AdminProperties adminProperties = new AdminProperties();
        char[] expectedPassword = "strongpassword".toCharArray();

        adminProperties.setPassword(expectedPassword);

        assertThat(adminProperties.getPassword()).isEqualTo(expectedPassword);
    }

    @Test
    void testFirstNameNotNull() {
        AdminProperties adminProperties = new AdminProperties();
        adminProperties.setLastName("Doe");
        adminProperties.setPassword("strongpassword123".toCharArray());

        assertThatThrownBy(() -> {
            var violations = validator.validate(adminProperties);
            if (!violations.isEmpty()) {
                throw new NullPointerException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(NullPointerException.class)
                .hasMessage("first name must not be null");
    }

    @Test
    void testLastNameNotNull() {
        AdminProperties adminProperties = new AdminProperties();
        adminProperties.setFirstName("John");
        adminProperties.setPassword("strongpassword123".toCharArray());

        assertThatThrownBy(() -> {
            var violations = validator.validate(adminProperties);
            if (!violations.isEmpty()) {
                throw new NullPointerException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(NullPointerException.class)
                .hasMessage("last name must not be null");
    }

    @Test
    void testPasswordNotEmpty() {
        AdminProperties adminProperties = new AdminProperties();
        adminProperties.setFirstName("John");
        adminProperties.setLastName("Doe");

        assertThatThrownBy(() -> {
            adminProperties.setPassword(new char[0]); // Устанавливаем пустой пароль
            var violations = validator.validate(adminProperties);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(IllegalArgumentException.class)
                .satisfies(ex -> {
                    String message = ex.getMessage();
                    assertTrue(message.contains("password must not be empty") || message.contains("password's length must be at least 10"),
                            "Expected exception message to contain 'password must not be empty' or 'password's length must be at least 10'");
                });
    }

    @Test
    void testPasswordMinimumLength() {
        AdminProperties adminProperties = new AdminProperties();
        adminProperties.setFirstName("John");
        adminProperties.setLastName("Doe");

        assertThatThrownBy(() -> {
            adminProperties.setPassword("short".toCharArray());
            var violations = validator.validate(adminProperties);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password's length must be at least 10");
    }
}
