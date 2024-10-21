package com.epam.gymcrmsystemapi.service.user;

import com.epam.gymcrmsystemapi.model.user.User;

public interface UserOperations {

    User save(String firstName, String lastName);

}
