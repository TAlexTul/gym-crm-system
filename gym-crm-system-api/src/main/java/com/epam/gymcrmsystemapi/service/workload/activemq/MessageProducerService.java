package com.epam.gymcrmsystemapi.service.workload.activemq;

import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingSaveRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService implements MessageProducer {

    @Value("${activemq.destination.add}")
    private String destinationAdd;
    @Value("${activemq.destination.delete}")
    private String destinationDelete;

    private final MessageProducerMQ producer;

    public MessageProducerService(MessageProducerActiveMQ producer) {
        this.producer = producer;
    }

    @Override
    public void processAndSend(ProvidedTrainingSaveRequest request) {
        producer.sendMessage(destinationAdd, request);
    }

    @Override
    public void processAndSend(ProvidedTrainingDeleteRequest request) {
        producer.sendMessage(destinationDelete, request);
    }
}
