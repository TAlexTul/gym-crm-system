package com.epam.trainerworkloadapi.service.activemq;

import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.service.summary.SummaryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumerService implements MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumerService.class);
    private final SummaryOperations summaryOperations;

    public MessageConsumerService(SummaryOperations summaryOperations) {
        this.summaryOperations = summaryOperations;
    }

    @Override
    @JmsListener(destination = "${activemq.destination.add}")
    public void receiveMessage(ProvidedTrainingSaveRequest request) {
        log.info("Receive message on 'activemq.destination.add' destination with data '{}'", request);
        summaryOperations.create(request);
    }

    @Override
    @JmsListener(destination = "${activemq.destination.delete}")
    public void receiveMessage(ProvidedTrainingDeleteRequest request) {
        log.info("Receive message on 'activemq.destination.delete' destination with data '{}'", request);
        summaryOperations.deleteProvidedTrainings(
                request.trainerUsernames(), request.trainingDate(), request.trainingDuration());
    }
}
