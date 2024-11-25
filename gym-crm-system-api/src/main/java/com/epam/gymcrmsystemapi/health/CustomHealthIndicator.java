package com.epam.gymcrmsystemapi.health;

import com.epam.gymcrmsystemapi.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("custom-indicator")
public class CustomHealthIndicator implements HealthIndicator {

    @Value("${custom.health.api.enabled}")
    private boolean enabled;
    @Value("${host}")
    private String host;

    private final RestTemplate restTemplate;

    public CustomHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        String url = host + Routes.HEALTH_API;
        if (!enabled) {
            return Health.up().withDetail("custom-status", "Disabled").build();
        }
        try {
            restTemplate.getForObject(url, String.class);
            return Health.up().withDetail("custom-status", "Available")
                    .withDetail("server-time", 1 + "ms")
                    .build();
        } catch (RuntimeException e) {
            return Health.down().withDetail("custom-status", "Unavailable").withException(e).build();
        }
    }
}
