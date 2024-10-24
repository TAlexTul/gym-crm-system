package com.epam.gymcrmsystemapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class RestTemplateConfigTest {

    @Test
    void testRestTemplateBeanCreation() {
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

        RestTemplate restTemplate = restTemplateConfig.restTemplate();

        assertThat(restTemplate).isNotNull();
    }
}
