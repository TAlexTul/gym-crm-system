package com.epam.gymcrmsystemapi.model.user;

import org.springframework.security.core.GrantedAuthority;

public enum KnownAuthority implements GrantedAuthority {

    ROLE_ADMIN,

    ROLE_TRAINEE,

    ROLE_TRAINER;

    @Override
    public String getAuthority() {
        return name();
    }
}
