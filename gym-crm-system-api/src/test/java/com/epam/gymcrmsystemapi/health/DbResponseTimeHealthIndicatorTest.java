package com.epam.gymcrmsystemapi.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class DbResponseTimeHealthIndicatorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DbResponseTimeHealthIndicator dbResponseTimeHealthIndicator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(dbResponseTimeHealthIndicator, "enabled", true);
        ReflectionTestUtils.setField(dbResponseTimeHealthIndicator, "db", "TestDB");
    }

    @Test
    void health_WhenEnabledAndDbIsAvailable_ReturnsUp() {
        doNothing().when(jdbcTemplate).execute("SELECT 1");

        Health health = dbResponseTimeHealthIndicator.health();

        Health expectedHealth = Health.up()
                .withDetail("database", "TestDB")
                .withDetail("status", "Available")
                .build();

        assertEquals(expectedHealth.getStatus(), health.getStatus());
        assertEquals(expectedHealth.getDetails().get("database"), health.getDetails().get("database"));
        assertEquals(expectedHealth.getDetails().get("status"), health.getDetails().get("status"));
    }

    @Test
    void health_WhenEnabledAndDbIsDown_ReturnsDown() {
        doThrow(new RuntimeException("Database is down")).when(jdbcTemplate).execute("SELECT 1");

        Health health = dbResponseTimeHealthIndicator.health();

        assertEquals(Health.down()
                .withDetail("database", "TestDB database is down")
                .withDetail("error", "Database is down")
                .build(), health);
    }

    @Test
    void health_WhenDisabled_ReturnsOutOfService() {
        ReflectionTestUtils.setField(dbResponseTimeHealthIndicator, "enabled", false);

        Health health = dbResponseTimeHealthIndicator.health();

        assertEquals(Health.outOfService()
                .withDetail("database", "TestDB health check is disabled")
                .build(), health);
    }
}
