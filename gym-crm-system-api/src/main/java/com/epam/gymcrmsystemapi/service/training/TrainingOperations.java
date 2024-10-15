package com.epam.gymcrmsystemapi.service.training;

import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.training.Type;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainingOperations {

    TrainingResponse create(TrainingSaveRequest request);

    Page<TrainingResponse> list(Pageable pageable);

    Page<TrainingResponse> filterBy(String traineeUsername, String trainerUsername,
                                    OffsetDateTime fromDate, OffsetDateTime toDate,
                                    Type trainingType, Pageable pageable);

    Page<TrainerResponse> listOfTrainersNotAssignedByTraineeUsername(String username, Pageable pageable);

    Optional<TrainingResponse> findById(long id);

    TrainingResponse updateTraineeSetOfTrainers(long id, List<String> trainersList);

}
