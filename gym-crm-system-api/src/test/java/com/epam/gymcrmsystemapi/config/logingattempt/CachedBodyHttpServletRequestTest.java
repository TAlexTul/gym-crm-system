package com.epam.gymcrmsystemapi.config.logingattempt;

import com.epam.gymcrmsystemapi.config.loggingattempt.CachedBodyHttpServletRequest;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CachedBodyHttpServletRequestTest {

    private CachedBodyHttpServletRequest cachedBodyHttpServletRequest;

    @BeforeEach
    void setUp() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String requestBody = "Hello, this is a test!";
        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

        ServletInputStream inputStream = new ServletInputStream() {
            private int index = 0;

            @Override
            public boolean isFinished() {
                return index >= requestBodyBytes.length;
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
                return index < requestBodyBytes.length ? requestBodyBytes[index++] : -1;
            }
        };

        when(request.getInputStream()).thenReturn(inputStream);

        cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
    }

    @Test
    void testGetInputStream() {
        String result = new BufferedReader(new InputStreamReader(cachedBodyHttpServletRequest.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        assertEquals("Hello, this is a test!", result);
    }

    @Test
    void testGetReader() {
        BufferedReader reader = cachedBodyHttpServletRequest.getReader();
        String result = reader.lines().collect(Collectors.joining("\n"));

        assertEquals("Hello, this is a test!", result);
    }
}
