package com.epam.gymcrmsystemapi.model.auth.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AccessTokenResponseTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeCorrectly() throws Exception {
        String accessToken = "sampleAccessToken";
        String refreshToken = "sampleRefreshToken";
        long expireIn = 3600L;
        AccessTokenResponse originalResponse = new AccessTokenResponse(accessToken, refreshToken, expireIn);

        String json = objectMapper.writeValueAsString(originalResponse);

        AccessTokenResponse deserializedResponse = objectMapper.readValue(json, AccessTokenResponse.class);

        assertThat(deserializedResponse.accessToken()).isEqualTo(originalResponse.accessToken());
        assertThat(deserializedResponse.refreshToken()).isEqualTo(originalResponse.refreshToken());
        assertThat(deserializedResponse.expireIn()).isEqualTo(originalResponse.expireIn());
    }
}
