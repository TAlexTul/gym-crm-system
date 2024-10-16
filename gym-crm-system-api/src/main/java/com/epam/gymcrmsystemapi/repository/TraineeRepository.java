package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @Query("select t from Trainee t where t.user.username = :username")
    Optional<Trainee> findByUsername(String username);

    @Query("select t from Trainee t where t.user.firstName = :firstName and t.user.lastName = :lastName")
    Optional<Trainee> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("select case when count (t) > 0 then true else false end " +
            "from Trainee t where t.user.firstName = :firstName and t.user.lastName = :lastName")
    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    @Query("delete from Trainee t where t.user.username = :username")
    @Modifying
    void deleteByUsername(String username);

}