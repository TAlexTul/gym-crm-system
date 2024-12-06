package com.epam.trainerworkloadapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoutesTest {

    @Test
    void testApiRoot() {
        assertEquals("/api/v1", Routes.API_ROOT, "API_ROOT should be '/api/v1'");
    }

    @Test
    void testWorkload() {
        assertEquals("/api/v1/workload", Routes.WORKLOAD, "WORKLOAD should be '/api/v1/workload'");
    }

    @Test
    void testTraining() {
        assertEquals("/api/v1/training", Routes.TRAINING, "TRAINING should be '/api/v1/training'");
    }
}
