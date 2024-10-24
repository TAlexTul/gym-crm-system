package com.epam.gymcrmsystemapi.service.user;

import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.model.user.request.OverrideLoginRequest;

public interface UserOperations {

    User save(String firstName, String lastName, KnownAuthority authority);

    void changeStatusById(Long id, UserStatus status);

    void changeStatusByUsername(String username, UserStatus status);

    void changeLoginDataById(long id, OverrideLoginRequest request);

    void changeLoginDataByUsername(String username, OverrideLoginRequest request);

}
