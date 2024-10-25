package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.service.trainee.TraineeOperations;
import com.epam.gymcrmsystemapi.service.trainer.TrainerOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.LOGIN)
@Tag(name = "Login-data",
        description = "Operations for updating login data in the application")
public class LoginDataController {

    private final TraineeOperations traineeOperations;
    private final TrainerOperations trainerOperations;

    public LoginDataController(TraineeOperations traineeOperations,
                               TrainerOperations trainerOperations) {
        this.traineeOperations = traineeOperations;
        this.trainerOperations = trainerOperations;
    }

    @PatchMapping(
            value = "/trainees/account",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change current trainee login data",
            description = "Update the login data of the current authenticated trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee login data updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse changeCurrentTraineeLoginData(/*@AuthenticationPrincipal*/ String username,
                                                         @RequestBody @Valid OverrideLoginRequest request) {
        return traineeOperations.changeLoginDataByUsername(username, request);
    }


    @PatchMapping(
            value = "/trainees/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change trainee login data by ID",
            description = "Update the login data of a trainee by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee login data updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TraineeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public TraineeResponse changeTraineeLoginData(@PathVariable long id,
                                                  @RequestBody @Valid OverrideLoginRequest request) {
        return traineeOperations.changeLoginDataById(id, request);
    }


    @PatchMapping(
            value = "/trainers/account",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update trainer's login data",
            description = "Change the login data for the currently authenticated trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public TrainerResponse changeCurrentTrainerLoginData(
            /*@AuthenticationPrincipal*/ String username, @RequestBody @Valid OverrideLoginRequest request) {
        return trainerOperations.changeLoginDataByUsername(username, request);
    }


    @PatchMapping(
            value = "/trainers/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change trainer's login data by ID",
            description = "Update the login data of a trainer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public TrainerResponse changeTrainerLoginData(@PathVariable long id,
                                                  @RequestBody @Valid OverrideLoginRequest request) {
        return trainerOperations.changeLoginDataById(id, request);
    }
}
