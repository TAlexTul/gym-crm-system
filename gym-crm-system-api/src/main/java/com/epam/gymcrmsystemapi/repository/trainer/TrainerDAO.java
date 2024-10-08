package com.epam.gymcrmsystemapi.repository.trainer;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TrainerDAO {

    Trainer save(Trainer trainer);

    Page<Trainer> findAll(Pageable pageable);

    Optional<Trainer> findById(long id);

    Optional<Trainer> findByFirstNameAndLastName(String firstName, String lastName);

    boolean existByFirstNameAndLastName(String firstName, String lastName);

    Trainer mergeById(long id, Trainer trainer);

    Trainer changeStatusById(long id, Trainer trainee);

    Trainer changePasswordById(long id, Trainer trainer);

    void deleteById(long id);

}
