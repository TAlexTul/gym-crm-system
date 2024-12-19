package com.epam.trainerworkloadapi.config.security;

import com.epam.trainerworkloadapi.Routes;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConstantsTest {

    @Test
    void testRoutesConstructorShouldThrowException() throws NoSuchMethodException {
        Constructor<Routes> constructor = Routes.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof AssertionError);
        assertEquals("non-instantiable class", exception.getCause().getMessage());
    }

    @Test
    void testAuthTokenPrefix() {
        String expected = "Bearer ";

        String actual = SecurityConstants.AUTH_TOKEN_PREFIX;

        assertEquals(expected, actual, "AUTH_TOKEN_PREFIX should match the expected value");
    }

    @Test
    void testAuthoritiesClaim() {
        String expected = "authorities";

        String actual = SecurityConstants.AUTHORITIES_CLAIM;

        assertEquals(expected, actual, "AUTHORITIES_CLAIM should match the expected value");
    }
}
