package com.epam.trainerworkloadapi.exceptions.handler;

import com.epam.trainerworkloadapi.exceptions.handler.ErrorResponse;
import com.epam.trainerworkloadapi.exceptions.handler.RestExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest {

    private final RestExceptionHandler restExceptionHandler = new RestExceptionHandler();

    @Test
    void handleMethodNotAllowedException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-uri");

        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("GET", Collections.singleton("POST"));

        ErrorResponse response = restExceptionHandler.handleMethodNotAllowedException(ex, request);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), response.status());
        assertEquals("/some-uri", response.path());
        assertEquals("Request method 'GET' is not supported", response.error()); // Убедитесь, что сообщение соответствует ожиданиям
        assertNotNull(response.timestamp());
    }

    @Test
    void handleValidationException() {
        // Создаем мок исключения
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        List<FieldError> fieldErrors = List.of(
                new FieldError("objectName", "fieldName", "Invalid value")
        );
        when(ex.getFieldErrors()).thenReturn(fieldErrors);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/validation-error");

        ErrorResponse response = restExceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("/validation-error", response.path());
        assertTrue(response.error().contains("fieldName: Invalid value"));
        assertNotNull(response.timestamp());
    }

    @Test
    void handleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/not-found");

        ResponseEntity<ErrorResponse> responseEntity =
                restExceptionHandler.handleResponseStatusException(ex, request);
        ErrorResponse response = responseEntity.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("/not-found", response.path());
        assertEquals("404 NOT_FOUND \"Not Found\"", response.error());
        assertNotNull(response.timestamp());
    }

    @Test
    void handleGenericException() {
        Exception exception = new Exception("Test exception message");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test/uri");

        ErrorResponse response = restExceptionHandler.handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("An unexpected error occurred: Test exception message", response.error());
        assertEquals("/test/uri", response.path());
        assertNotNull(response.timestamp());
    }
}
