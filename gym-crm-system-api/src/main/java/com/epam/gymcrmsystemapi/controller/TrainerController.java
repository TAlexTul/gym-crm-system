package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.ChangeUserStatusRequest;
import com.epam.gymcrmsystemapi.service.trainer.TrainerOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping(Routes.TRAINERS)
@Tag(name = "Trainer",
        description = "Operations for creating, updating, retrieving and deleting trainer in the application")
public class TrainerController {

    private final TrainerOperations trainerOperations;

    public TrainerController(TrainerOperations trainerOperations) {
        this.trainerOperations = trainerOperations;
    }

    //region trainer registration

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new trainer",
            description = "Register a new trainer and return the registration response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TrainerRegistrationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<TrainerRegistrationResponse> register(
            @RequestBody @Valid TrainerSaveRequest request, UriComponentsBuilder ucb) {
        TrainerRegistrationResponse response = trainerOperations.create(request);
        return ResponseEntity
                .created(ucb.path(Routes.TRAINERS + "/{id}").build(response.id()))
                .body(response);
    }

    //endregion

    //region authenticated trainer API

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PageableAsQueryParam
    @Operation(summary = "List all trainers", description = "Retrieve a paginated list of all trainers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainers retrieved successfully")
    })
    public Page<TrainerResponse> listTrainers(@Parameter(hidden = true) Pageable pageable) {
        return trainerOperations.list(pageable);
    }

    @GetMapping(
            value = "/not-assigned",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "List unassigned trainers",
            description = "Retrieve a list of trainers not assigned to a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of unassigned trainers retrieved successfully")
    })
    public List<TrainerResponse> listTrainersNotAssignedByTraineeUsername(@RequestParam String username) {
        return trainerOperations.listOfTrainersNotAssignedByTraineeUsername(username);
    }

    @GetMapping(
            value = "/account",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get current trainer",
            description = "Retrieve details of the currently authenticated trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public TrainerResponse getCurrentTrainer(/*@AuthenticationPrincipal*/ String username) {
        return trainerOperations.findByUsername(username)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(username));
    }

    @PatchMapping(
            value = "/account",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update current trainer",
            description = "Update details of the currently authenticated trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public TrainerResponse mergeCurrentTrainer(
            /*@AuthenticationPrincipal*/ String username, @RequestBody @Valid TrainerMergeRequest request) {
        return trainerOperations.mergeByUsername(username, request);
    }

    @DeleteMapping("/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current trainer", description = "Delete the currently authenticated trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public void deleteCurrentTrainer(/*@AuthenticationPrincipal*/ String username) {
        trainerOperations.deleteByUsername(username);
    }

    //endregion

    //region admin-only API

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get trainer by ID", description = "Retrieve details of a trainer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public TrainerResponse getTrainerById(@PathVariable long id) {
        return trainerOperations.findById(id)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(id));
    }

    @PatchMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update trainer by ID", description = "Update details of a trainer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public TrainerResponse mergeTrainerById(@PathVariable long id,
                                            @RequestBody @Valid TrainerMergeRequest request) {
        return trainerOperations.mergeById(id, request);
    }

    @PatchMapping(
            value = "/{id}/status",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change trainer's status by ID",
            description = "Update the status of a trainer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer's status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public TrainerResponse changeTrainerStatusById(@PathVariable long id,
                                                   @RequestBody @Valid ChangeUserStatusRequest request) {
        return trainerOperations.changeStatusById(id, request.status());
    }

    @PatchMapping(
            value = "/status",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change trainer's status by username",
            description = "Update the status of a trainer by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer's status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public TrainerResponse changeTrainerStatusByUsername(@RequestParam String username,
                                                         @RequestBody @Valid ChangeUserStatusRequest request) {
        return trainerOperations.changeStatusByUsername(username, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete trainer by ID", description = "Delete a trainer's account by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public void deleteTrainerById(@PathVariable long id) {
        trainerOperations.deleteById(id);
    }

    //endregion

}
