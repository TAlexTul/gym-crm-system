package com.epam.gymcrmsystemapi.exceptions.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(GymRestExceptionHandler.class)
class GymRestExceptionHandlerTest {

    @InjectMocks
    private GymRestExceptionHandler gymRestExceptionHandler;

    @Test
    void handleMethodNotAllowedException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");

        MethodNotAllowedException ex = new MethodNotAllowedException("GET", Collections.singleton(HttpMethod.POST));

        ErrorResponse response = gymRestExceptionHandler.handleMethodNotAllowedException(ex, request);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), response.status());
        assertEquals("/some-uri", response.path());
        assertEquals("405 METHOD_NOT_ALLOWED \"Request method 'GET' is not supported.\"", response.error());
        assertNotNull(response.timestamp());
    }

    @Test
    void handleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/validation-error");

        ErrorResponse response = gymRestExceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("/validation-error", response.path());
        assertNotNull(response.timestamp());
    }

    @Test
    void handleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/not-found");

        ResponseEntity<ErrorResponse> responseEntity =
                gymRestExceptionHandler.handleResponseStatusException(ex, request);
        ErrorResponse response = responseEntity.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("/not-found", response.path());
        assertEquals("404 NOT_FOUND \"Not Found\"", response.error());
        assertNotNull(response.timestamp());
    }

    @Test
    public void testHandleGenericException() {
        Exception exception = new Exception("Test exception message");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test/uri");

        ErrorResponse response = gymRestExceptionHandler.handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("An unexpected error occurred: Test exception message", response.error());
        assertEquals("/test/uri", response.path());
    }
}
