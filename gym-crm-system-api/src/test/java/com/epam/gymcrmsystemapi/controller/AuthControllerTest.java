package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.response.AccessTokenResponse;
import com.epam.gymcrmsystemapi.service.auth.AuthOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {

    private MockMvc mvc;

    private AuthOperations authOperations;

    @BeforeEach
    void setUp() {
        authOperations = mock(AuthOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authOperations))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRefresh() throws Exception {
        String tokenForRefreshing = "tokenForRefreshing";
        var response = new AccessTokenResponse(
                "expectedAccessToken",
                "expectedRefreshToken",
                600
        );

        when(authOperations.refreshToken(tokenForRefreshing)).thenReturn(response);

        var expectedJson = """
                {
                  "accessToken": "expectedAccessToken",
                  "refreshToken": "expectedRefreshToken",
                  "expireIn": 600
                }
                """;

        mvc.perform(post(Routes.TOKEN + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                    {
                                       "refreshToken": "tokenForRefreshing"
                                     }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(authOperations, only()).refreshToken(tokenForRefreshing);
    }
}
