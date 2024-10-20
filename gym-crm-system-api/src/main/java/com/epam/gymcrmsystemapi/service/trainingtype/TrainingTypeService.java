package com.epam.gymcrmsystemapi.service.trainingtype;

import com.epam.gymcrmsystemapi.model.training.type.response.TrainingTypeResponse;
import com.epam.gymcrmsystemapi.repository.TrainingTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingTypeService implements TrainingTypeOperations {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeService(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingTypeResponse> list() {
        return trainingTypeRepository.findAll().stream()
                .map(TrainingTypeResponse::fromTrainingType)
                .toList();
    }
}
