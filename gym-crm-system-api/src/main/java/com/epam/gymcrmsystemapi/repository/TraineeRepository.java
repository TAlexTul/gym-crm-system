package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @Query("select t from Trainee t where t.user.username = :username")
    Optional<Trainee> findByUsername(String username);

    @Query("delete from User u where u.username = :username")
    @Modifying
    void deleteByUsername(String username);

}
