package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.SignInRequest;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.TOKEN)
@Tag(name = "Authentication", description = "Operations for authentication in the application")
public class AuthController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = SignInRequest.class)))
    public ResponseEntity login() {
        return ResponseEntity.ok().build();
    }
}
