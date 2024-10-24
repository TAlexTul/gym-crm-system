package com.epam.gymcrmsystemapi.config.loggingattempt;

import com.epam.gymcrmsystemapi.service.loggingattempt.LoggingAttemptOperations;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BruteForceProtectionFilter extends GenericFilterBean {

    private final LoggingAttemptOperations loggingAttemptOperations;

    public BruteForceProtectionFilter(LoggingAttemptOperations loggingAttemptOperations) {
        this.loggingAttemptOperations = loggingAttemptOperations;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(httpRequest);

        if (HttpMethod.POST.name().equalsIgnoreCase(cachedBodyHttpServletRequest.getMethod()) &&
                MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(cachedBodyHttpServletRequest.getContentType())) {

            String jsonBody = new String(cachedBodyHttpServletRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();

            if (jsonBody.isBlank()) {
                return;
            }
            Map<String, String> requestBody = objectMapper.readValue(jsonBody, new TypeReference<>() {
            });

            if (requestBody.isEmpty()) {
                chain.doFilter(cachedBodyHttpServletRequest, response);
                return;
            }

            String username = requestBody.get("login");

            if (username != null && loggingAttemptOperations.isBlocked(username)) {
                httpResponse.sendError(HttpStatus.LOCKED.value(), "User account is locked for: " + username);
                return;
            }
        }

        chain.doFilter(cachedBodyHttpServletRequest, response);
    }
}
