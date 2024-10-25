package com.epam.gymcrmsystemapi.service.user;

import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;

public interface UserOperations {

    User save(String firstName, String lastName);

    void changeStatusById(Long id, UserStatus status);

    void changeStatusByUsername(String username, UserStatus status);

    void changeLoginDataById(long id, OverrideLoginRequest request);

    void changeLoginDataByUsername(String username, OverrideLoginRequest request);

}
