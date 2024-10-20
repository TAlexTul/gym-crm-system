package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.training.type.response.TrainingTypeResponse;
import com.epam.gymcrmsystemapi.service.trainingtype.TrainingTypeOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingTypeControllerTest {

    private MockMvc mvc;

    private TrainingTypeOperations trainingTypeOperations;

    @BeforeEach
    void setUp() {
        trainingTypeOperations = mock(TrainingTypeOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new TrainingTypeController(trainingTypeOperations))
                .build();
    }

    @Test
    void testGetTrainingTypes() throws Exception {

        List<TrainingTypeResponse> responses = getTrainingTypeResponse();

        when(trainingTypeOperations.list()).thenReturn(responses);

        var expectedJson = getExpectedJson();

        mvc.perform(get(Routes.TRAINING_TYPES))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(trainingTypeOperations, only()).list();
    }

    private List<TrainingTypeResponse> getTrainingTypeResponse() {
        return List.of(
                new TrainingTypeResponse(0, Type.STRENGTH_TRAINING),
                new TrainingTypeResponse(1, Type.CARDIO_WORKOUT),
                new TrainingTypeResponse(2, Type.FUNCTIONAL_TRAINING),
                new TrainingTypeResponse(3, Type.CROSSFIT_WORKOUT),
                new TrainingTypeResponse(4, Type.PILATES_SESSION),
                new TrainingTypeResponse(5, Type.BODYBUILDING_PROGRAM),
                new TrainingTypeResponse(6, Type.MARTIAL_ARTS_TRAINING),
                new TrainingTypeResponse(7, Type.SWIMMING_SESSION),
                new TrainingTypeResponse(8, Type.GROUP_FITNESS_CLASS),
                new TrainingTypeResponse(9, Type.FITNESS_AEROBICS),
                new TrainingTypeResponse(10, Type.REHABILITATION_WORKOUT),
                new TrainingTypeResponse(11, Type.NUTRITION_AND_DIET_PLAN),
                new TrainingTypeResponse(12, Type.CYCLING_WORKOUT),
                new TrainingTypeResponse(13, Type.GYMNASTICS_TRAINING),
                new TrainingTypeResponse(14, Type.TRX_TRAINING),
                new TrainingTypeResponse(15, Type.SPECIAL_NEEDS_TRAINING),
                new TrainingTypeResponse(16, Type.STRETCHING_SESSION),
                new TrainingTypeResponse(17, Type.BOOTCAMP_WORKOUT)
        );
    }

    private String getExpectedJson() {
        return """
                [
                  {
                    "id": 0,
                    "type": "STRENGTH_TRAINING"
                  },
                  {
                    "id": 1,
                    "type": "CARDIO_WORKOUT"
                  },
                  {
                    "id": 2,
                    "type": "FUNCTIONAL_TRAINING"
                  },
                  {
                    "id": 3,
                    "type": "CROSSFIT_WORKOUT"
                  },
                  {
                    "id": 4,
                    "type": "PILATES_SESSION"
                  },
                  {
                    "id": 5,
                    "type": "BODYBUILDING_PROGRAM"
                  },
                  {
                    "id": 6,
                    "type": "MARTIAL_ARTS_TRAINING"
                  },
                  {
                    "id": 7,
                    "type": "SWIMMING_SESSION"
                  },
                  {
                    "id": 8,
                    "type": "GROUP_FITNESS_CLASS"
                  },
                  {
                    "id": 9,
                    "type": "FITNESS_AEROBICS"
                  },
                  {
                    "id": 10,
                    "type": "REHABILITATION_WORKOUT"
                  },
                  {
                    "id": 11,
                    "type": "NUTRITION_AND_DIET_PLAN"
                  },
                  {
                    "id": 12,
                    "type": "CYCLING_WORKOUT"
                  },
                  {
                    "id": 13,
                    "type": "GYMNASTICS_TRAINING"
                  },
                  {
                    "id": 14,
                    "type": "TRX_TRAINING"
                  },
                  {
                    "id": 15,
                    "type": "SPECIAL_NEEDS_TRAINING"
                  },
                  {
                    "id": 16,
                    "type": "STRETCHING_SESSION"
                  },
                  {
                    "id": 17,
                    "type": "BOOTCAMP_WORKOUT"
                  }
                ]
                """;
    }
}
