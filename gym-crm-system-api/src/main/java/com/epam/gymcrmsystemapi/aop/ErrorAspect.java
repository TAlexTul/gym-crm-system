package com.epam.gymcrmsystemapi.aop;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorAspect {

    private final MeterRegistry registry;

    public ErrorAspect(MeterRegistry registry) {
        this.registry = registry;
    }

    @Before("execution(public * com.epam.gymcrmsystemapi.exceptions.handler.GymRestExceptionHandler.*(..))")
    public void countError() {
        registry.counter("error.counter").increment();
    }
}
