package com.epam.gymcrmsystemapi.service.loggingattempt;

public interface LoggingAttemptOperations {

    void loginFailed(String username);

    void loginSucceeded(String username);

    boolean isBlocked(String username);

}
