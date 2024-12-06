package com.epam.trainerworkloadapi.controller;

import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.service.TrainingOperations;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(Routes.TRAINING)
public class ProvidedTrainingController {

    private final TrainingOperations trainingOperations;

    public ProvidedTrainingController(TrainingOperations trainingOperations) {
        this.trainingOperations = trainingOperations;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
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
    public void deleteProvidedTraining(
            @RequestParam String trainerUsernames,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime trainingDate,
            @RequestParam long trainingDuration) {
        trainingOperations.deleteProvidedTrainings(trainerUsernames, trainingDate, trainingDuration);
    }
}
