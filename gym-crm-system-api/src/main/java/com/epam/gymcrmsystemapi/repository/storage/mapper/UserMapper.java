package com.epam.gymcrmsystemapi.repository.storage.mapper;

import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.springframework.stereotype.Service;

@Service
public class UserMapper implements DataMapper<User> {

    @Override
    public User map(String[] data) {
        Long userId = Long.parseLong(data[0]);
        String fistName = data[1];
        String lastName = data[2];
        String userName = data[3];
        String password = data[4];
        UserStatus status = UserStatus.valueOf(data[5]);

        var user = new User();
        user.setId(userId);
        user.setFirstName(fistName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setPassword(password);
        user.setStatus(status);

        return user;
    }
}
