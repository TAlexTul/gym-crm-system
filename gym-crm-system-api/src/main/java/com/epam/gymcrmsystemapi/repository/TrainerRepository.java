package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("select t from Trainer t where t.id not in "
            + "(select trainer.id from Training tr join tr.trainers trainer join tr.trainees trainee where trainee = :trainee)")
    Page<Trainer> findAllNotAssignedToTrainee(Trainee trainee, Pageable pageable);

    @Query("select t from Trainer t where t.user.username = :username")
    Optional<Trainer> findByUsername(String username);

    @Query("select t from Trainer t where t.user.firstName = :firstName and t.user.lastName = :lastName")
    Optional<Trainer> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("select case when count(t) > 0 then true else false end " +
            "from Trainer t where t.user.firstName = :firstName and t.user.lastName = :lastName")
    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    @Query("delete from Trainer t where t.user.username = :username")
    @Modifying
    void deleteByUsername(String username);

}
