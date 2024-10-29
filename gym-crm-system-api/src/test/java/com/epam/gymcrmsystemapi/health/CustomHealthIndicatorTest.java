package com.epam.gymcrmsystemapi.health;

import com.epam.gymcrmsystemapi.Routes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CustomHealthIndicatorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CustomHealthIndicator customHealthIndicator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(customHealthIndicator, "host", "http://localhost:8080");
    }

    @Test
    void health_WhenEnabledAndApiAvailable_ReturnsUp() {
        ReflectionTestUtils.setField(customHealthIndicator, "enabled", true);
        String url = "http://localhost:8080" + Routes.HEALTH_API;

        when(restTemplate.getForObject(url, String.class)).thenReturn("Remote API is running");

        Health health = customHealthIndicator.health();

        assertEquals(Health.up().withDetail("status", "Available").build(), health);
    }

    @Test
    void health_WhenDisabled_ReturnsUpWithDisabledStatus() {
        ReflectionTestUtils.setField(customHealthIndicator, "enabled", false);

        Health health = customHealthIndicator.health();

        assertEquals(Health.up().withDetail("status", "Disabled").build(), health);
    }
}
