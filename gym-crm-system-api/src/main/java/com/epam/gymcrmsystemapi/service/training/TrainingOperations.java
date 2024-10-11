package com.epam.gymcrmsystemapi.service.training;

import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TrainingOperations {

    TrainingResponse create(TrainingSaveRequest request);

    Page<TrainingResponse> list(Pageable pageable);

    Optional<TrainingResponse> findById(long id);

}
