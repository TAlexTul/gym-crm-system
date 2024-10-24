package com.epam.gymcrmsystemapi.config.security.properties;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityPropertiesTest {

    @Test
    void testJwtProperties() {
        SecurityProperties securityProperties = new SecurityProperties();
        JWTProperties jwtProperties = new JWTProperties();
        jwtProperties.setSecret("mySecret");
        jwtProperties.setAccessExpireIn(Duration.ofMinutes(15));
        jwtProperties.setRefreshExpireIn(Duration.ofDays(5));

        securityProperties.setJwt(jwtProperties);

        assertThat(securityProperties.getJwt()).isEqualTo(jwtProperties);
        assertThat(securityProperties.getJwt().getSecret()).isEqualTo("mySecret");
        assertThat(securityProperties.getJwt().getAccessExpireIn()).isEqualTo(Duration.ofMinutes(15));
        assertThat(securityProperties.getJwt().getRefreshExpireIn()).isEqualTo(Duration.ofDays(5));
    }

    @Test
    void testAdminsMapNotNull() {
        SecurityProperties securityProperties = new SecurityProperties();
        Map<String, AdminProperties> admins = new HashMap<>();

        securityProperties.setAdmins(admins);

        assertThat(securityProperties.getAdmins()).isEqualTo(admins);
    }

    @Test
    void testAdminsMapContainsValidAdminProperties() {
        SecurityProperties securityProperties = new SecurityProperties();
        Map<String, AdminProperties> admins = new HashMap<>();
        AdminProperties adminProperties = new AdminProperties();
        adminProperties.setFirstName("John");
        adminProperties.setLastName("Doe");
        adminProperties.setPassword("StrongPassword123".toCharArray());
        admins.put("admin1", adminProperties);

        securityProperties.setAdmins(admins);

        assertThat(securityProperties.getAdmins()).containsEntry("admin1", adminProperties);
    }
}
