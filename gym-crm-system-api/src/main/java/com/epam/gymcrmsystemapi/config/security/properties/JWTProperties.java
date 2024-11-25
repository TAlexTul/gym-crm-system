package com.epam.gymcrmsystemapi.config.security.properties;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;

public class JWTProperties {

    @NotEmpty(message = "secret must not be empty")
    private String secret;

    @DurationMax(minutes = 30, message = "must be less than or equal to 30 minutes")
    @DurationMin(minutes = 1, message = "must be greater than or equal to 1 minute")
    private Duration accessExpireIn;

    @DurationMax(days = 7, message = "must be less than or equal to 7 days")
    @DurationMin(hours = 12, message = "must be greater than or equal to 12 hours")
    private Duration refreshExpireIn;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getAccessExpireIn() {
        return accessExpireIn;
    }

    public void setAccessExpireIn(Duration accessExpireIn) {
        this.accessExpireIn = accessExpireIn;
    }

    public Duration getRefreshExpireIn() {
        return refreshExpireIn;
    }

    public void setRefreshExpireIn(Duration refreshExpireIn) {
        this.refreshExpireIn = refreshExpireIn;
    }
}
