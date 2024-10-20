package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.training.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {

    List<Training> findAllByTrainees(Trainee trainees);

    Optional<Training> findByTrainingNameLike(String trainingName);

}
