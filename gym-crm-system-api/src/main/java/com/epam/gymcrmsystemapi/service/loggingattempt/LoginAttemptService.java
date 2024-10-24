package com.epam.gymcrmsystemapi.service.loggingattempt;

import com.epam.gymcrmsystemapi.model.loginattempt.LoginAttempt;
import com.epam.gymcrmsystemapi.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class LoginAttemptService implements LoggingAttemptOperations {

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 5; // Minutes

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Override
    public void loginFailed(String username) {
        LoginAttempt loginAttempt = loginAttemptRepository.findByUsername(username)
                .orElse(new LoginAttempt(username, 0, OffsetDateTime.now()));

        loginAttempt.setAttempts(loginAttempt.getAttempts() + 1);
        loginAttempt.setLastModified(OffsetDateTime.now());
        loginAttemptRepository.save(loginAttempt);
    }

    @Override
    public void loginSucceeded(String username) {
        loginAttemptRepository.findByUsername(username)
                .ifPresent(loginAttemptRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlocked(String username) {
        return loginAttemptRepository.findByUsername(username)
                .filter(attempt -> attempt.getAttempts() >= MAX_ATTEMPTS)
                .filter(attempt -> attempt.getLastModified()
                        .isAfter(OffsetDateTime.now().minusMinutes(LOCK_TIME_DURATION)))
                .isPresent();
    }
}
