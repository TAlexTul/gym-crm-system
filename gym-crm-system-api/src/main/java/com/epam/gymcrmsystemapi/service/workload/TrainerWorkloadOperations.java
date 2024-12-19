package com.epam.gymcrmsystemapi.service.workload;

import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;

public interface TrainerWorkloadOperations {

    void invoke(TrainingSaveRequest request, TrainingResponse response);

    void invoke(TrainingResponse response);

}
