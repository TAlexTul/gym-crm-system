package com.epam.trainerworkloadapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigTest {

    @Test
    void testObjectMapperBeanConfiguration() {
        JacksonConfig config = new JacksonConfig();

        ObjectMapper objectMapper = config.objectMapper();

        assertNotNull(objectMapper, "ObjectMapper bean should not be null");

        boolean javaTimeModuleRegistered = objectMapper.getRegisteredModuleIds().stream()
                .anyMatch(id -> id.toString().contains("jackson-datatype-jsr310"));
        assertTrue(javaTimeModuleRegistered,
                "JavaTimeModule 'jackson-datatype-jsr310' should be registered in ObjectMapper");

        assertDoesNotThrow(() -> objectMapper.writeValueAsString(java.time.LocalDate.now()),
                "ObjectMapper should handle Java 8 date/time types");
    }
}
