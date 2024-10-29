package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.HEALTH_API)
@Tag(name = "Health", description = "Operations for checking API in the application")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check API Health",
            description = "Returns a message indicating that the remote API is running.")
    public String getHealthApi() {
        return "Remote API is running";
    }
}