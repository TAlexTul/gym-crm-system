package com.epam.trainerworkloadapi.repository;

import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SummaryTrainingsDurationRepository extends MongoRepository<SummaryTrainingsDuration, String> {

    Optional<SummaryTrainingsDuration> findByUsername(String username);

}
