package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.exceptions.TrainingExceptions;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.service.training.TrainingOperations;
import com.epam.gymcrmsystemapi.service.workload.TrainerWorkloadOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(Routes.TRAININGS)
@Tag(name = "Training", description = "Operations for creating, retrieving training in the application")
public class TrainingController {

    private final TrainingOperations trainingOperations;
    private final TrainerWorkloadOperations trainerWorkloadOperations;

    public TrainingController(TrainingOperations trainingOperations,
                              TrainerWorkloadOperations trainerWorkloadOperations) {
        this.trainingOperations = trainingOperations;
        this.trainerWorkloadOperations = trainerWorkloadOperations;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new training",
            description = "Create a new training and return the training response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<TrainingResponse> register(@RequestBody @Valid TrainingSaveRequest request,
                                                     UriComponentsBuilder ucb) {
        TrainingResponse response = trainingOperations.create(request);
        trainerWorkloadOperations.invoke(request, response);
        return ResponseEntity
                .created(ucb.path(Routes.TRAININGS + "/{id}").build(response.id()))
                .body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PageableAsQueryParam
    @Operation(summary = "List all trainings", description = "Retrieve a paginated list of all trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully")
    })
    public Page<TrainingResponse> listTrainings(@Parameter(hidden = true) Pageable pageable) {
        return trainingOperations.list(pageable);
    }

    @GetMapping(
            value = "/filter",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PageableAsQueryParam
    @Operation(summary = "Filter trainings",
            description = "Retrieve a paginated list of trainings based on filter criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered list of trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No trainings found matching the criteria")
    })
    public Page<TrainingResponse> filterBy(
            @RequestParam(required = false) String traineeUsername,
            @RequestParam(required = false) String trainerUsername,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate,
            @RequestParam(required = false) Type trainingType,
            @Parameter(hidden = true) Pageable pageable) {
        return trainingOperations.filterBy(traineeUsername, trainerUsername, fromDate, toDate, trainingType, pageable);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get training by ID", description = "Retrieve a training by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Training not found")
    })
    public TrainingResponse getTrainingById(@PathVariable long id) {
        return trainingOperations.findById(id).orElseThrow(() -> TrainingExceptions.trainingNotFound(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete training by ID",
            description = "Delete a training by its unique identifier and update the trainer's workload."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Training deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Training not found with the provided ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void deleteTrainingById(@PathVariable long id) {
        TrainingResponse response = trainingOperations.deleteById(id)
                .orElseThrow(() -> TrainingExceptions.trainingNotFound(id));
        trainerWorkloadOperations.invoke(response);
    }
}
