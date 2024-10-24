package com.epam.gymcrmsystemapi.config.security.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "gym-crm-system.security")
public class SecurityProperties {

    @Valid
    @NestedConfigurationProperty
    private JWTProperties jwt;

    private Map<@NotBlank String, @Valid AdminProperties> admins;

    public JWTProperties getJwt() {
        return jwt;
    }

    public void setJwt(JWTProperties jwt) {
        this.jwt = jwt;
    }

    public Map<String, AdminProperties> getAdmins() {
        return admins;
    }

    public void setAdmins(Map<String, AdminProperties> admins) {
        this.admins = admins;
    }
}
