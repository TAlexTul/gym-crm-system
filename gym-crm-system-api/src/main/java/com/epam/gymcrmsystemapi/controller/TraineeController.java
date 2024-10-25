package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeChangeTrainersSetRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.ChangeUserStatusRequest;
import com.epam.gymcrmsystemapi.service.trainee.TraineeOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping(Routes.TRAINEES)
@Tag(name = "Trainee",
        description = "Operations for creating, updating, retrieving and deleting trainee in the application")
public class TraineeController {

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);
    private final TraineeOperations traineeOperations;

    public TraineeController(TraineeOperations traineeOperations) {
        this.traineeOperations = traineeOperations;
    }

    //region trainee registration

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new trainee",
            description = "Register a new trainee and return the registration response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee registered successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeRegistrationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<TraineeRegistrationResponse> register(@RequestBody @Valid TraineeSaveRequest request,
                                                                UriComponentsBuilder ucb) {
        TraineeRegistrationResponse response = traineeOperations.create(request);
        return ResponseEntity
                .created(ucb.path(Routes.TRAINEES + "/{id}").build(response.id()))
                .body(response);
    }

    //endregion

    //region authenticated trainee API

    @GetMapping(
            value = "/account",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get current trainee information",
            description = "Retrieve the current authenticated trainee's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee information retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse getCurrentTrainee(/*@AuthenticationPrincipal*/ String username) {
        return traineeOperations.findByUsername(username)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(username));
    }

    @PatchMapping(
            value = "/account",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update current trainee information",
            description = "Update the current authenticated trainee's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee information updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse mergeCurrentTrainee(/*@AuthenticationPrincipal*/ String username,
                                                                            @RequestBody @Valid TraineeMergeRequest request) {
        return traineeOperations.mergeByUsername(username, request);
    }

    @DeleteMapping("/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current trainee",
            description = "Delete the current authenticated trainee's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public void deleteCurrentTrainee(/*@AuthenticationPrincipal*/ String username) {
        traineeOperations.deleteByUsername(username);
    }

    //endregion

    //region admin-only API

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PageableAsQueryParam
    @Operation(summary = "List all trainees", description = "Retrieve a paginated list of all trainees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainees retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "No trainees found")
    })
    public Page<TraineeResponse> listTrainees(@Parameter(hidden = true) Pageable pageable) {
        return traineeOperations.list(pageable);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get trainee by ID", description = "Retrieve a trainee's information by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse getTraineeById(@PathVariable long id) {
        log.info("Fetching entity with id: {}", id);
        return traineeOperations.findById(id)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(id));
    }

    @PatchMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update trainee by ID", description = "Update a trainee's information by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse mergeTraineeById(@PathVariable long id,
                                            @RequestBody @Valid TraineeMergeRequest request) {
        return traineeOperations.mergeById(id, request);
    }

    @PatchMapping(
            value = "/{id}/status",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change trainee status by ID",
            description = "Change the status of a trainee by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee status updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse changeTraineeStatusById(@PathVariable long id,
                                                   @RequestBody @Valid ChangeUserStatusRequest request) {
        return traineeOperations.changeStatusById(id, request.status());
    }

    @PatchMapping(
            value = "/status",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change trainee status by username",
            description = "Change the status of a trainee by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee status updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse changeTraineeStatusByUsername(@RequestParam String username,
                                                         @RequestBody @Valid ChangeUserStatusRequest request) {
        return traineeOperations.changeStatusByUsername(username, request.status());
    }

    @PatchMapping(
            value = "/trainers/change",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Change trainee's set of trainers",
            description = "Update the set of trainers for a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee's set of trainers updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public List<TrainerResponse> changeTrainersSet(@RequestBody @Valid TraineeChangeTrainersSetRequest request) {
        return traineeOperations.changeTraineeSetOfTrainers(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete trainee by ID", description = "Delete a trainee's account by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public void deleteTraineeById(@PathVariable long id) {
        traineeOperations.deleteById(id);
    }

    //endregion

}
