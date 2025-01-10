package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.training.type.response.TrainingTypeResponse;
import com.epam.gymcrmsystemapi.service.trainingtype.TrainingTypeOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Routes.TRAINING_TYPES)
@Tag(name = "Training type", description = "Operations for retrieving training type in the application")
public class TrainingTypeController {

    private final TrainingTypeOperations trainingTypeOperations;

    public TrainingTypeController(TrainingTypeOperations trainingTypeOperations) {
        this.trainingTypeOperations = trainingTypeOperations;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all training types", description = "Retrieve a list of all training types available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of training types retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required")
    })
    public List<TrainingTypeResponse> getTrainingTypes() {
        return trainingTypeOperations.list();
    }
}
