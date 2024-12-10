package com.epam.trainerworkloadapi.service.activemq;

import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;

public interface MessageConsumer {

    void receiveMessage(ProvidedTrainingSaveRequest request);

    void receiveMessage(ProvidedTrainingDeleteRequest request);

}
