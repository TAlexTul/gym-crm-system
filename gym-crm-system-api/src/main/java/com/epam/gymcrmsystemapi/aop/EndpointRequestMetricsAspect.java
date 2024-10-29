package com.epam.gymcrmsystemapi.aop;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EndpointRequestMetricsAspect {

    private final MeterRegistry registry;

    public EndpointRequestMetricsAspect(MeterRegistry registry) {
        this.registry = registry;
    }

    @After("execution(public * com.epam.gymcrmsystemapi.controller.TraineeController.listTrainees(..))")
    public void countUserRequests() {
        registry.counter("endpoint.requests", "endpoint", "/trainees").increment();
    }
}
