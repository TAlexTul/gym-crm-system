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
        ReflectionTestUtils.setField(customHealthIndicator, "host", "http://localhost:8080");

        String url = "http://localhost:8080" + Routes.HEALTH_API;

        when(restTemplate.getForObject(url, String.class)).thenReturn("Remote API is running");

        Health health = customHealthIndicator.health();

        assertEquals(Health.up().build().getStatus(), health.getStatus(),
                "Expected health status to be UP");
        assertEquals("Available", health.getDetails().get("custom-status"),
                "Expected custom-status to be Available");
        assertEquals("1ms", health.getDetails().get("server-time"),
                "Expected server-time to be 1ms");
    }

    @Test
    void health_WhenDisabled_ReturnsUpWithDisabledStatus() {
        ReflectionTestUtils.setField(customHealthIndicator, "enabled", false);

        Health health = customHealthIndicator.health();

        assertEquals(Health.up().withDetail("custom-status", "Disabled").build(), health);
    }
}
