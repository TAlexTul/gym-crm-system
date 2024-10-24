package com.epam.gymcrmsystemapi.model.auth;

import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;

import java.util.EnumSet;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final User source;

    public CustomUserDetails(User source) {
        super(source.getUsername(),
                source.getPassword(),
                source.getStatus() == UserStatus.ACTIVE,
                true,
                true,
                true,
                EnumSet.copyOf(source.getAuthorities().keySet())
        );
        this.source = source;
    }

    public User getSource() {
        return source;
    }
}
