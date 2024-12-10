package com.epam.trainerworkloadapi.controller;

import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import com.epam.trainerworkloadapi.service.TrainingOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Month;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProvidedTrainingControllerTest {

    private MockMvc mvc;

    private TrainingOperations trainingOperations;

    @BeforeEach
    void setUp() {
        trainingOperations = mock(TrainingOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new ProvidedTrainingController(trainingOperations))
                .build();
    }

    @Test
    void testCreateProvidedTraining() throws Exception {
        var request = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                OffsetDateTime.parse("2024-12-08T12:21:22.541Z"),
                30000);

        var response = new ProvidedTrainingResponse(
                1L,
                2024,
                Month.JANUARY,
                30000L
        );

        when(trainingOperations.create(request)).thenReturn(response);

        var expectedJson = """
                {
                  "id": 1,
                  "year": 2024,
                  "month": "JANUARY",
                  "trainingDuration": 30000
                }
                """;

        mvc.perform(post(Routes.TRAINING)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                  "trainerUsername": "Jane.Jenkins",
                                  "trainerFirstName": "Jane",
                                  "trainerLastName": "Jenkins",
                                  "trainerStatus": "ACTIVE",
                                  "trainingDate": "2024-12-08T12:21:22.541Z",
                                  "trainingDuration": 30000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(trainingOperations, only()).create(request);
    }

    @Test
    void testDeleteProvidedTraining() throws Exception {
        String trainerUsernames = "Jane.Jenkins";
        OffsetDateTime trainingDate = OffsetDateTime.now();
        long trainingDuration = 30000;

        doNothing().when(trainingOperations).deleteProvidedTrainings(trainerUsernames, trainingDate, trainingDuration);

        mvc.perform(delete(Routes.TRAINING + "?trainerUsernames=" + "Jane.Jenkins&trainingDate="
                        + trainingDate + "&trainingDuration=" + trainingDuration))
                .andExpect(status().isNoContent());

        verify(trainingOperations, only()).deleteProvidedTrainings(trainerUsernames, trainingDate, trainingDuration);
    }
}
