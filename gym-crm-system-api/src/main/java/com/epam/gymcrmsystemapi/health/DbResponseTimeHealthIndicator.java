package com.epam.gymcrmsystemapi.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("custom-db-response-time")
public class DbResponseTimeHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(DbResponseTimeHealthIndicator.class);

    @Value("${custom.health.db-response-time.enabled}")
    private boolean enabled;

    private final String db;
    private final JdbcTemplate jdbcTemplate;

    public DbResponseTimeHealthIndicator(@Value("${db-name}") String db, JdbcTemplate jdbcTemplate) {
        this.db = db;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        if (!enabled) {
            log.info("DbResponseTimeHealthIndicator is disabled.");
            return Health.outOfService()
                    .withDetail("database", db + " health check is disabled")
                    .build();
        }

        try {
            long responseTime = getResponseTime();
            return Health.up()
                    .withDetail("database", db)
                    .withDetail("response_time", responseTime + "ms")
                    .withDetail("status", "Available")
                    .build();
        } catch (Exception e) {
            log.error("Database health check failed: " + e.getMessage());

            return Health.down()
                    .withDetail("database", db + " database is down")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private long getResponseTime() {
        long startTime = System.currentTimeMillis();
        jdbcTemplate.execute("SELECT 1");
        return System.currentTimeMillis() - startTime;
    }
}
