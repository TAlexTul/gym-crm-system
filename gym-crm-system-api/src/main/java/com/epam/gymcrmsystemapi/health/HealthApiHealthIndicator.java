package com.epam.gymcrmsystemapi.health;

import com.epam.gymcrmsystemapi.Routes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthApiHealthIndicator implements HealthIndicator {

    @Value("${custom.health.api.enabled}")
    private boolean enabled;
    @Value("${host}")
    private String host;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        String url = host + Routes.HEALTH_API;
        if (!enabled) {
            return Health.up().withDetail("status", "Disabled").build();
        }
        try {
            restTemplate.getForObject(url, String.class);
            return Health.up().withDetail("status", "Available").build();
        } catch (RuntimeException e) {
            return Health.down().withDetail("status", "Unavailable").withException(e).build();
        }
    }
}
