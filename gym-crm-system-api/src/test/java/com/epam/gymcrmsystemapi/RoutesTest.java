package com.epam.gymcrmsystemapi;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class RoutesTest {

    @Test
    void testRoutesConstructorShouldThrowException() throws NoSuchMethodException {
        Constructor<Routes> constructor = Routes.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof AssertionError);
        assertEquals("non-instantiable class", exception.getCause().getMessage());
    }

    @Test
    void testRoutesConstants() {
        assertEquals("/api/v1", Routes.API_ROOT);
        assertEquals("/api/v1/trainees", Routes.TRAINEES);
        assertEquals("/api/v1/trainers", Routes.TRAINERS);
        assertEquals("/api/v1/trainings", Routes.TRAININGS);
        assertEquals("/api/v1/training-types", Routes.TRAINING_TYPES);
        assertEquals("/api/v1/token", Routes.TOKEN);
    }
}
