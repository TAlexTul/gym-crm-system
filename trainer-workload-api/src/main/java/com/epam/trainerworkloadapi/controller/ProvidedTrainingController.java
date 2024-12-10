package com.epam.trainerworkloadapi.controller;

import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.service.TrainingOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(Routes.TRAINING)
@Tag(name = "Provided Training Controller", description = "API for managing provided trainings by trainers.")
public class ProvidedTrainingController {

    private final TrainingOperations trainingOperations;

    public ProvidedTrainingController(TrainingOperations trainingOperations) {
        this.trainingOperations = trainingOperations;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a training",
            description = "Creates a new provided training by a trainer and returns the created training details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Training successfully created."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<ProvidedTrainingResponse> createProvidedTraining(
            @RequestBody ProvidedTrainingSaveRequest request,
            UriComponentsBuilder ucb) {
        var response = trainingOperations.create(request);
        return ResponseEntity
                .created(ucb.path(Routes.TRAINING + "/{id}").build(response.id()))
                .body(response);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a training",
            description = "Deletes a provided training based on the trainer's name, date, and duration."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Training successfully deleted."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public void deleteProvidedTraining(
            @RequestParam String trainerUsernames,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime trainingDate,
            @RequestParam long trainingDuration) {
        trainingOperations.deleteProvidedTrainings(trainerUsernames, trainingDate, trainingDuration);
    }
}
