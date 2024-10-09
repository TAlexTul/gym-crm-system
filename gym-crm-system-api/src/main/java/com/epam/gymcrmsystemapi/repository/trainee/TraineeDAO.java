package com.epam.gymcrmsystemapi.repository.trainee;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TraineeDAO {

    Trainee save(Trainee trainee);

    Page<Trainee> findAll(Pageable pageable);

    Optional<Trainee> findById(long id);

    Optional<Trainee> findByFirstNameAndLastName(String firstName, String lastName);

    boolean existByFirstNameAndLastName(String firstName, String lastName);

    Trainee changeById(long id, Trainee trainee);

    void deleteById(long id);

}
