package com.epam.gymcrmsystemapi.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeterRegistryConfigTest {

    @Test
    void testMeterRegistryBeanCreation() {
        MeterRegistryConfig meterRegistryConfig = new MeterRegistryConfig();

        MeterRegistry meterRegistry = meterRegistryConfig.meterRegistry();

        assertThat(meterRegistry).isNotNull();
    }
}
