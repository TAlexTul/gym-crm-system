package com.epam.trainerworkloadapi.controller;

import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.service.TrainingOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.WORKLOAD)
public class TrainerWorkloadController {

    private final TrainingOperations summaryTrainingsOperations;
    private final TrainingOperations trainingOperations;

    public TrainerWorkloadController(TrainingOperations summaryTrainingsOperations,
                                     TrainingOperations trainingOperations) {
        this.summaryTrainingsOperations = summaryTrainingsOperations;
        this.trainingOperations = trainingOperations;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public MonthlySummaryTrainingsResponse getSummaryTrainings(@RequestBody MonthlySummaryTrainingsRequest request) {
        MonthlySummaryTrainingsResponse response = summaryTrainingsOperations.findSummaryTrainings(request);
        trainingOperations.deleteTrainingsByYearMonth(request.yearMonth());
        return response;
    }
}
