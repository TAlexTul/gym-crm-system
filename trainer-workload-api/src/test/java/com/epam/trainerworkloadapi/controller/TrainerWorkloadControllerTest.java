package com.epam.trainerworkloadapi.controller;

import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import com.epam.trainerworkloadapi.service.TrainingOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Month;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainerWorkloadControllerTest {

    private MockMvc mvc;

    private TrainingOperations summaryTrainingsOperations;

    private TrainingOperations trainingOperations;

    @BeforeEach
    void setUp() {
        summaryTrainingsOperations = mock(TrainingOperations.class);
        trainingOperations = mock(TrainingOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new TrainerWorkloadController(summaryTrainingsOperations, trainingOperations))
                .build();
    }

    @Test
    void testGetSummaryTrainings() throws Exception {
        var request = new MonthlySummaryTrainingsRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                YearMonth.parse("2024-01")
        );

        var response = new MonthlySummaryTrainingsResponse(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                List.of(new ProvidedTrainingResponse(1L, 2024, Month.JANUARY, 30000L)),
                30000L
        );

        when(summaryTrainingsOperations.findSummaryTrainings(request)).thenReturn(response);
        doNothing().when(trainingOperations).deleteTrainingsByYearMonth(request.yearMonth());

        var expectedJson = """
                {
                  "trainerUsername": "Jane.Jenkins",
                  "trainerFirstName": "Jane",
                  "trainerLastName": "Jenkins",
                  "trainerStatus": "ACTIVE",
                  "trainings": [
                    {
                      "id": 1,
                      "year": 2024,
                      "month": "JANUARY",
                      "trainingDuration": 30000
                    }
                  ],
                  "trainingSummaryDuration": 30000
                }
                """;

        mvc.perform(post(Routes.WORKLOAD)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                  "trainerUsername": "Jane.Jenkins",
                                  "trainerFirstName": "Jane",
                                  "trainerLastName": "Jenkins",
                                  "trainerStatus": "ACTIVE",
                                  "yearMonth": "2024-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(summaryTrainingsOperations, only()).findSummaryTrainings(request);
        verify(trainingOperations, only()).deleteTrainingsByYearMonth(request.yearMonth());
    }
}
