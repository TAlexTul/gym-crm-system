package com.epam.gymcrmsystemapi.service.workload;

import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.service.workload.activemq.MessageProducer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerWorkloadService implements TrainerWorkloadOperations {

    private final MessageProducer messageProducer;

    public TrainerWorkloadService(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @Override
    public void invoke(TrainingSaveRequest request, TrainingResponse response) {
        TrainerResponse trainerResponse = response.trainers().stream().findFirst()
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(request.trainerUsername()));

        var providedRequest = new ProvidedTrainingSaveRequest(
                request.trainerUsername(),
                trainerResponse.firstName(),
                trainerResponse.lastName(),
                trainerResponse.status(),
                response.trainingDate(),
                response.trainingDuration()
        );

        messageProducer.processAndSend(providedRequest);
    }

    @Override
    public void invoke(TrainingResponse response) {
        List<String> trainerUsernames = response.trainers().stream()
                .map(TrainerResponse::username)
                .toList();

        var request = new ProvidedTrainingDeleteRequest(
                String.join(",", trainerUsernames),
                response.trainingDate(),
                response.trainingDuration()
        );

        messageProducer.processAndSend(request);
    }
}
