package com.epam.gymcrmsystemapi.aop;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EndpointRequestMetricsAspectTest {

    @Mock
    private MeterRegistry registry;

    @Mock
    private Counter counter;

    private EndpointRequestMetricsAspect endpointRequestMetricsAspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        endpointRequestMetricsAspect = new EndpointRequestMetricsAspect(registry);

        when(registry.counter("endpoint.requests", "endpoint", "/trainees")).thenReturn(counter);
    }

    @Test
    void countUserRequests() {
        endpointRequestMetricsAspect.countUserRequests();

        verify(registry).counter("endpoint.requests", "endpoint", "/trainees");
        verify(counter).increment();
    }
}
