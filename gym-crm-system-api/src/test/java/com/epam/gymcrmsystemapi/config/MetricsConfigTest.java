package com.epam.gymcrmsystemapi.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricsConfigTest {

    @Test
    void testMeterRegistryBeanCreation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MetricsConfig.class)) {
            MeterRegistry meterRegistry = context.getBean(MeterRegistry.class);

            assertNotNull(meterRegistry);
            assertTrue(meterRegistry instanceof PrometheusMeterRegistry);
        }
    }
}
