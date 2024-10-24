package com.epam.gymcrmsystemapi.model.auth;

import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserAuthority;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomUserDetailsTest {

    @Test
    void shouldInitializeCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        user.getAuthorities().put(KnownAuthority.ROLE_TRAINEE, new UserAuthority());

        Map<KnownAuthority, UserAuthority> authorities = new HashMap<>();
        authorities.put(KnownAuthority.ROLE_ADMIN, new UserAuthority());
        user.setAuthorities(authorities);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getUsername()).isEqualTo("John.Doe");
        assertThat(userDetails.getPassword()).isEqualTo("aB9dE4fGhJ");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getSource()).isEqualTo(user);
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }
}
