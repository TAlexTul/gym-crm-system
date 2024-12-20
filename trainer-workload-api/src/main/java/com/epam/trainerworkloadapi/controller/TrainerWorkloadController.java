package com.epam.trainerworkloadapi.controller;

import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.service.summary.SummaryOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.WORKLOAD)
@Tag(name = "Trainer Workload", description = "APIs for managing trainer workload")
public class TrainerWorkloadController {

    private final SummaryOperations summaryOperations;

    public TrainerWorkloadController(SummaryOperations summaryOperations) {
        this.summaryOperations = summaryOperations;
    }

    @PostMapping(consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get Monthly Summary of Trainings",
            description = "Fetches a summary of training sessions for a specific month and deletes the " +
                    "corresponding training records.",
            tags = {"Trainer Workload"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully fetched the monthly summary of trainings.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    public MonthlySummaryTrainingsResponse getSummaryTrainingsByYearMonth(
            @RequestBody MonthlySummaryTrainingsRequest request) {
        return summaryOperations.getMonthlySummaryTrainingsDuration(request);
    }
}
