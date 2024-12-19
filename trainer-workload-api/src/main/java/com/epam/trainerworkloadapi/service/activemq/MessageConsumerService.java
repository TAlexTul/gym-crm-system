package com.epam.trainerworkloadapi.service.activemq;

import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.service.TrainingOperations;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumerService implements MessageConsumer {

    private final TrainingOperations trainingOperations;

    public MessageConsumerService(TrainingOperations trainingOperations) {
        this.trainingOperations = trainingOperations;
    }

    @Override
    @JmsListener(destination = "${activemq.destination.add}")
    public void receiveMessage(ProvidedTrainingSaveRequest request) {
        trainingOperations.create(request);
    }

    @Override
    @JmsListener(destination = "${activemq.destination.delete}")
    public void receiveMessage(ProvidedTrainingDeleteRequest request) {
        trainingOperations.deleteProvidedTrainings(
                request.trainerUsernames(), request.trainingDate(), request.trainingDuration());
    }
}
