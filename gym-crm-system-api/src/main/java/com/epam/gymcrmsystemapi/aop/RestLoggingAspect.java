package com.epam.gymcrmsystemapi.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}

    @Before("restController() && args(request,..)")
    public void logBefore(HttpServletRequest request) {
        log.info("Called endpoint: {} {}", request.getMethod(), request.getRequestURI());
    }

    @AfterReturning(pointcut = "restController()", returning = "response")
    public void logAfterReturning(Object response) {
        log.info("Response: {}", response);
    }

    @AfterThrowing(pointcut = "restController()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        log.error("Error during REST call: {}", ex.getMessage(), ex);
    }
}
