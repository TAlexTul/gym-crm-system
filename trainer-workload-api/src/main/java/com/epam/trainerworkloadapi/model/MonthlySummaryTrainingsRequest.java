package com.epam.trainerworkloadapi.model;

import com.epam.trainerworkloadapi.model.user.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.YearMonth;

public record MonthlySummaryTrainingsRequest(

        @NotNull(message = "trainer trainerUsername must not be null")
        String trainerUsername,

        @NotNull(message = "trainer first name must not be null")
        String trainerFirstName,

        @NotNull(message = "trainer last name must not be null")
        String trainerLastName,

        @NotNull(message = "trainer user status must not be null")
        UserStatus trainerStatus,

        @NotNull(message = "year and month must not be null")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
        @Schema(type = "string", example = "2024-04")
        YearMonth yearMonth

) {
}
