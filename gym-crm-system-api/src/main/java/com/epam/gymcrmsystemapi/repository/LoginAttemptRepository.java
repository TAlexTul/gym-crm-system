package com.epam.gymcrmsystemapi.repository;

import com.epam.gymcrmsystemapi.model.loginattempt.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    Optional<LoginAttempt> findByUsername(String username);

}
