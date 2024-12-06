package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.user.request.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.service.user.UserOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

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

//    @Test
//    void shouldChangeCurrentUserLoginData() throws Exception {
//        // Установим имя аутентифицированного пользователя
//        var authenticatedUser = "authenticatedUser";
//
//        // Мокируем аутентификацию
//        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
//        when(authentication.getName()).thenReturn(authenticatedUser);
//        SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        // Создаем запрос с данными для изменения логина
//        OverrideLoginRequest request = new OverrideLoginRequest(
//                "John.Doe",
//                "oldPassword",
//                "newPassword");
//
//        // Мокируем сервис
//        doNothing().when(userOperations).changeLoginDataByUsername(authenticatedUser, request);
//
//        // Выполняем запрос PATCH
//        mvc.perform(patch(Routes.LOGIN + "/account")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                 {
//                                   "username": "John.Doe",
//                                   "oldPassword": "oldPassword",
//                                   "newPassword": "newPassword"
//                                 }
//                            """))
//                .andExpect(status().isOk());
//
//        // Проверяем, что метод был вызван с правильными параметрами
//    }
}
