package com.epam.gymcrmsystemapi.repository.training;

import com.epam.gymcrmsystemapi.model.training.Training;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TrainingDAO {

    Training save(Training training);

    Page<Training> findAll(Pageable pageable);

    Optional<Training> findById(long id);

}
