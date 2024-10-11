package com.epam.gymcrmsystemapi.service.training;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.repository.trainee.TraineeDAO;
import com.epam.gymcrmsystemapi.repository.trainer.TrainerDAO;
import com.epam.gymcrmsystemapi.repository.training.TrainingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService implements TrainingOperations {

    @Autowired
    private TraineeDAO traineeDAO;
    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TrainingDAO trainingDAO;

    @Override
    public TrainingResponse create(TrainingSaveRequest request) {
        return TrainingResponse.fromTraining(save(request));
    }

    private Training save(TrainingSaveRequest request) {
        Trainee trainee = traineeDAO.findById(request.traineeId())
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(request.traineeId()));
        Trainer trainer = trainerDAO.findById(request.trainerId())
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(request.trainerId()));

        var training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(request.trainingName());
        training.setTrainingType(request.trainingType());
        training.setTrainingDate(request.trainingDate());
        training.setTrainingDuration(request.trainingDuration());
        return trainingDAO.save(training);
    }

    @Override
    public Page<TrainingResponse> list(Pageable pageable) {
        return trainingDAO.findAll(pageable)
                .map(TrainingResponse::fromTrainingWithBasicAttributes);
    }

    @Override
    public Optional<TrainingResponse> findById(long id) {
        return trainingDAO.findById(id)
                .map(TrainingResponse::fromTraining);
    }
}
