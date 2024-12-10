package com.epam.trainerworkloadapi.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}

    @AfterReturning(pointcut = "restController()", returning = "response")
    public void logAfterReturning(Object response) {
        log.info("Response: {}", response);
    }

    @AfterThrowing(pointcut = "restController()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        log.error("Error during REST call: {}", ex.getMessage(), ex);
    }
}
