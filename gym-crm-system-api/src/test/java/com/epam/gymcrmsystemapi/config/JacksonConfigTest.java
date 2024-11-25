package com.epam.gymcrmsystemapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JacksonConfigTest {

    @Test
    void testObjectMapperBean() {
        JacksonConfig jacksonConfig = new JacksonConfig();

        ObjectMapper objectMapper = jacksonConfig.objectMapper();

        assertThat(objectMapper).isNotNull();
    }
}
