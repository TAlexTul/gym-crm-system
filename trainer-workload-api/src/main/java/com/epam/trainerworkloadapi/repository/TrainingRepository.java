package com.epam.trainerworkloadapi.repository;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends MongoRepository<ProvidedTraining, String> {

//    Optional<ProvidedTraining> findByUserUsernameAndTrainingDateAndTrainingDuration(
//            String username, OffsetDateTime trainingDate, long trainingDuration);
//
//    @Query("{'user.username': ?0, 'trainingDate': {$gte: ?1, $lt: ?2}}")
//    List<ProvidedTraining> findByUserUsernameAndMonth(
//            String trainerUsername, OffsetDateTime startDate, OffsetDateTime endDate);
//
//    @Query("{'user': ?0}")
//    Optional<ProvidedTraining> findByUserId(String userId);
//
//    @Modifying
//    @Query("{'trainingDate': {$gte: ?0, $lt: ?1}}")
//    void deleteByTrainingDateBetween(OffsetDateTime startDate, OffsetDateTime endDate);

}
