package com.epam.gymcrmsystemapi.model.auth;

import com.epam.gymcrmsystemapi.model.user.User;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RefreshTokenTest {

    @Test
    void testRefreshTokenInitialization() {
        UUID tokenValue = UUID.randomUUID();
        User user = new User();
        OffsetDateTime issuedAt = OffsetDateTime.now();
        OffsetDateTime expireAt = issuedAt.plusDays(30);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setValue(tokenValue);
        refreshToken.setUser(user);
        refreshToken.setIssuedAt(issuedAt);
        refreshToken.setExpireAt(expireAt);

        assertThat(refreshToken.getValue()).isEqualTo(tokenValue);
        assertThat(refreshToken.getUser()).isEqualTo(user);
        assertThat(refreshToken.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(refreshToken.getExpireAt()).isEqualTo(expireAt);
        assertThat(refreshToken.getPrevious()).isEmpty();
        assertThat(refreshToken.getNext()).isNull();
    }

    @Test
    void testSetNextToken() {
        RefreshToken refreshToken1 = new RefreshToken();
        RefreshToken refreshToken2 = new RefreshToken();

        refreshToken1.setNext(refreshToken2);

        assertThat(refreshToken1.getNext()).isEqualTo(refreshToken2);
    }
}
