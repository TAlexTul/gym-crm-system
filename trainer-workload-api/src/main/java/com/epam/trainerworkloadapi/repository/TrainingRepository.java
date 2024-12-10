package com.epam.trainerworkloadapi.repository;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<ProvidedTraining, Long> {

    Optional<ProvidedTraining> findByUserUsernameAndTrainingDateAndTrainingDuration(
            String username, OffsetDateTime trainingDate, long trainingDuration);

    @Query("select pt from ProvidedTraining pt where pt.user.username = :trainerUsername " +
            "and function('YEAR', pt.trainingDate) = :year and function('MONTH', pt.trainingDate) = :month")
    List<ProvidedTraining> findByUserUsernameAndMonth(@Param("trainerUsername") String trainerUsername,
                                                      @Param("year") int year,
                                                      @Param("month") int month);

    @Modifying
    @Query("delete from ProvidedTraining pt where pt.trainingDate >= :startDate and pt.trainingDate < :endDate")
    void deleteByTrainingDateBetween(@Param("startDate") OffsetDateTime startDate,
                                     @Param("endDate") OffsetDateTime endDate);

}
