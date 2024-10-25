package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.service.user.UserOperations;
import io.swagger.v3.oas.annotations.Operation;
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

    private final UserOperations userOperations;

    public LoginDataController(UserOperations userOperations) {
        this.userOperations = userOperations;
    }

    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change user login data by ID",
            description = "Update the login data of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User login data updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void changeTraineeLoginData(@PathVariable long id,
                                                  @RequestBody @Valid OverrideLoginRequest request) {
        userOperations.changeLoginDataById(id, request);
    }

    @PatchMapping(
            value = "/account",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change current user login data",
            description = "Update the login data of the current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User login data updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void changeCurrentTraineeLoginData(/*@AuthenticationPrincipal*/ String username,
                                                         @RequestBody @Valid OverrideLoginRequest request) {
        userOperations.changeLoginDataByUsername(username, request);
    }
}
