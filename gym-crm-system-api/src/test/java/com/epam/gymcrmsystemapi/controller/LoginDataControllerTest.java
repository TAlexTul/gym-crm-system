package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.user.request.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.service.user.UserOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginDataControllerTest {

    private MockMvc mvc;

    private UserOperations userOperations;

    @BeforeEach
    void setUp() {
        userOperations = mock(UserOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new LoginDataController(userOperations))
                .build();
    }

    @Test
    void testChangeUserLoginData() throws Exception {
        var traineeId = 1L;
        var request = new OverrideLoginRequest("John.Doe", "oldPassword", "newPassword");

        doNothing().when(userOperations).changeLoginDataById(traineeId, request);

        mvc.perform(patch(Routes.LOGIN + "/" + traineeId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                     {
                                       "username": "John.Doe",
                                       "oldPassword": "oldPassword",
                                       "newPassword": "newPassword"
                                     }
                                """))
                .andExpect(status().isOk());

        verify(userOperations, only()).changeLoginDataById(traineeId, request);
    }
}
