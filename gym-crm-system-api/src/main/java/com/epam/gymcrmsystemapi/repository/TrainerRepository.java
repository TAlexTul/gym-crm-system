package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("select t from Trainer t where not exists (select 1 from t.trainees trainee where trainee = :trainee)")
    List<Trainer> findAllNotAssignedToTrainee(Trainee trainee);

    @Query("select t from Trainer t where t.user.username = :username")
    Optional<Trainer> findByUsername(String username);

    @Query("select t from Trainer t where t.user.firstName = :firstName and t.user.lastName = :lastName")
    Optional<Trainer> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("select case when count(t) > 0 then true else false end " +
            "from Trainer t where t.user.firstName = :firstName and t.user.lastName = :lastName")
    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    @Query("select case when count(t) > 0 then true else false end from Trainer t where t.user.username = :username")
    boolean existsByUsername(String username);

    @Query("delete from User u where u.username = :username")
    @Modifying
    void deleteByUsername(String username);

}
