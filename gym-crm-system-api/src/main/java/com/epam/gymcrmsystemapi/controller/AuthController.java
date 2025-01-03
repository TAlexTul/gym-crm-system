package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.exceptions.auth.AuthorityExceptions;
import com.epam.gymcrmsystemapi.exceptions.auth.InvalidRefreshTokenException;
import com.epam.gymcrmsystemapi.model.auth.CustomUserDetails;
import com.epam.gymcrmsystemapi.model.auth.request.RefreshTokenRequest;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import com.epam.gymcrmsystemapi.model.auth.response.AccessTokenResponse;
import com.epam.gymcrmsystemapi.service.auth.AuthOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.TOKEN)
@Tag(name = "Authentication", description = "Operations for authentication in the application")
public class AuthController {

    private final AuthOperations authOperations;

    public AuthController(AuthOperations authOperations) {
        this.authOperations = authOperations;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = SignInRequest.class)))
    @Operation(
            summary = "Login endpoint",
            description = "Authenticates a user and returns an access token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Sign-in request containing user credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignInRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful authentication, returns an access token",
                            content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized, invalid credentials provided",
                            content = @Content)
    })
    public AccessTokenResponse login(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return authOperations.getToken(userDetails);
    }

    @PostMapping(
            value = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Access Token Refresh",
            description = "Method for refreshing an access token using a refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token",
                    content = @Content)
    })
    public AccessTokenResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        try {
            return authOperations.refreshToken(request.refreshToken());
        } catch (InvalidRefreshTokenException e) {
            throw AuthorityExceptions.invalidRefreshToken(e);
        }
    }

    @PostMapping(value = "/invalidate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Invalidate of refresh token",
            description = "Method for invalidating the current user's refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token successfully cancelled"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token",
                    content = @Content)
    })
    public void invalidate(@RequestBody @Valid RefreshTokenRequest request,
                           @AuthenticationPrincipal String username) {
        try {
            authOperations.invalidateToken(request.refreshToken(), username);
        } catch (InvalidRefreshTokenException e) {
            throw AuthorityExceptions.invalidRefreshToken(e);
        }
    }
}
