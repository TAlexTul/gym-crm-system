package com.epam.gymcrmsystemapi.model.auth.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SingInRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeCorrectly() throws Exception {
        String login = "John.Doe";
        String password = "password123";
        SignInRequest originalRequest = new SignInRequest(login, password);

        String json = objectMapper.writeValueAsString(originalRequest);

        SignInRequest deserializedRequest = objectMapper.readValue(json, SignInRequest.class);

        assertThat(deserializedRequest.login()).isEqualTo(originalRequest.login());
        assertThat(deserializedRequest.password()).isEqualTo(originalRequest.password());
    }

    @Test
    void shouldDeserializeWithAlias() throws Exception {
        String json = "{\"username\": \"John.Doe\", \"password\": \"password123\"}";

        SignInRequest request = objectMapper.readValue(json, SignInRequest.class);

        assertThat(request.login()).isEqualTo("John.Doe");
        assertThat(request.password()).isEqualTo("password123");
    }
}
