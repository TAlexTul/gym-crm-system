package com.epam.trainerworkloadapi.service.activemq;

import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.service.TrainingOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class MessageMessageConsumerTest {

    @Mock
    private TrainingOperations trainingOperations;

    @InjectMocks
    private MessageConsumerService messageConsumerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveMessage_Add() {
        ProvidedTrainingSaveRequest request = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                null,
                null,
                90L
        );

        messageConsumerService.receiveMessage(request);

        verify(trainingOperations).create(request);
    }

    @Test
    void testReceiveMessage_Delete() {
        ProvidedTrainingDeleteRequest request = new ProvidedTrainingDeleteRequest(
                "Jane.Jenkins",
                null,
                90L
        );

        messageConsumerService.receiveMessage(request);

        verify(trainingOperations).deleteProvidedTrainings(
                request.trainerUsernames(),
                request.trainingDate(),
                request.trainingDuration()
        );
    }
}
