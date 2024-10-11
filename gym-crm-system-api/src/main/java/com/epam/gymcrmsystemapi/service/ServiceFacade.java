package com.epam.gymcrmsystemapi.service;

import com.epam.gymcrmsystemapi.service.trainee.TraineeOperations;
import com.epam.gymcrmsystemapi.service.trainer.TrainerOperations;
import com.epam.gymcrmsystemapi.service.training.TrainingOperations;
import org.springframework.stereotype.Service;

@Service
public class ServiceFacade {

    private final TraineeOperations traineeOperations;
    private final TrainerOperations trainerOperations;
    private final TrainingOperations trainingOperations;

    public ServiceFacade(TraineeOperations traineeOperations,
                         TrainerOperations trainerOperations,
                         TrainingOperations trainingOperations) {
        this.traineeOperations = traineeOperations;
        this.trainerOperations = trainerOperations;
        this.trainingOperations = trainingOperations;
    }
}
