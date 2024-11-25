package com.epam.gymcrmsystemapi.config.logingattempt;

import com.epam.gymcrmsystemapi.config.loggingattempt.BruteForceProtectionFilter;
import com.epam.gymcrmsystemapi.config.loggingattempt.CachedBodyHttpServletRequest;
import com.epam.gymcrmsystemapi.service.loggingattempt.LoggingAttemptOperations;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

public class BruteForceProtectionFilterTest {

    private BruteForceProtectionFilter bruteForceProtectionFilter;
    private LoggingAttemptOperations loggingAttemptOperations;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        loggingAttemptOperations = mock(LoggingAttemptOperations.class);
        bruteForceProtectionFilter = new BruteForceProtectionFilter(loggingAttemptOperations);
        httpRequest = mock(HttpServletRequest.class);
        httpResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void testDoFilter_UserNotBlocked() throws IOException, ServletException {
        String requestBody = "{\"login\": \"John.Doe\"}";
        when(httpRequest.getMethod()).thenReturn("POST");
        when(httpRequest.getContentType()).thenReturn("application/json");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(httpRequest.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return inputStream.read();
            }
        });

        when(loggingAttemptOperations.isBlocked("John.Doe")).thenReturn(false);

        bruteForceProtectionFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(any(CachedBodyHttpServletRequest.class), eq(httpResponse));
    }

    @Test
    void testDoFilter_UserBlocked() throws IOException, ServletException {
        String requestBody = "{\"login\": \"John.Doe\"}";
        when(httpRequest.getMethod()).thenReturn("POST");
        when(httpRequest.getContentType()).thenReturn("application/json");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(httpRequest.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return inputStream.read();
            }
        });

        when(loggingAttemptOperations.isBlocked("John.Doe")).thenReturn(true);

        bruteForceProtectionFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(httpResponse).sendError(HttpStatus.LOCKED.value(), "User account is locked for: John.Doe");
        verify(filterChain, never()).doFilter(any(CachedBodyHttpServletRequest.class), eq(httpResponse));
    }

    @Test
    void testDoFilter_EmptyBody() throws IOException, ServletException {
        String requestBody = "{}";
        when(httpRequest.getMethod()).thenReturn("POST");
        when(httpRequest.getContentType()).thenReturn("application/json");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(httpRequest.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return inputStream.read();
            }
        });

        bruteForceProtectionFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(any(CachedBodyHttpServletRequest.class), eq(httpResponse));
    }

    @Test
    void testDoFilter_NoLoginField() throws IOException, ServletException {
        String requestBody = "{\"otherField\": \"value\"}";
        when(httpRequest.getMethod()).thenReturn("POST");
        when(httpRequest.getContentType()).thenReturn("application/json");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(httpRequest.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return inputStream.read();
            }
        });

        bruteForceProtectionFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain).doFilter(any(CachedBodyHttpServletRequest.class), eq(httpResponse));
    }
}

