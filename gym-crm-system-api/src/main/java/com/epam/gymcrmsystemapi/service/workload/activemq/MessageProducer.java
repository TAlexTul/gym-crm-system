package com.epam.gymcrmsystemapi.service.workload.activemq;

import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingSaveRequest;

public interface MessageProducer {

    void processAndSend(ProvidedTrainingSaveRequest request);

    void processAndSend(ProvidedTrainingDeleteRequest request);

}
