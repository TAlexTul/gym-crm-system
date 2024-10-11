package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.TrainingType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private final TrainingMapper trainingMapper = new TrainingMapper();

    @Test
    void testMap() {
        String[] data = {
                "1",
                "Strength Training",
                "STRENGTH",
                "2024-10-05T10:15:30+00:00",
                "PT1H30M"
        };

        Training result = trainingMapper.map(data);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Strength Training", result.getTrainingName());
        assertEquals(TrainingType.STRENGTH, result.getTrainingType());
        assertEquals(OffsetDateTime.parse("2024-10-05T10:15:30+00:00"), result.getTrainingDate());
        assertEquals(Duration.parse("PT1H30M"), result.getTrainingDuration());
    }
}
