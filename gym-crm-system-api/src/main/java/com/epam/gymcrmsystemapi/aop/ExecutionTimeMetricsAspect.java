package com.epam.gymcrmsystemapi.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeMetricsAspect {

    private final MeterRegistry registry;

    public ExecutionTimeMetricsAspect(MeterRegistry registry) {
        this.registry = registry;
    }

    @Around("execution(public * com.epam.gymcrmsystemapi.service.*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) {
        Timer timer = registry.timer("service.execution.time", "method", joinPoint.getSignature().getName());
        return timer.record(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
    }
}
