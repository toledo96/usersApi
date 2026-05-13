package com.security.practice.users.controller;

import com.security.practice.users.exception.RolNotFoundException;
import com.security.practice.users.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import com.security.practice.users.config.SecurityConfig;
import com.security.practice.users.service.UserService;
import com.security.practice.users.util.JwtUtil;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = UserController.class)
@Import({SecurityConfig.class, JwtUtil.class, AdviceController.class})
class AdviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void handleUserNotFound() throws Exception {
        Mockito.when(userService.getUserById(1L))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andDo(print()) // imprime el JSON en consola
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleRolNotFound() throws Exception {
        Mockito.doThrow(new RolNotFoundException("Rol no encontrado"))
                .when(userService).assignRoleToUser(1L, "ROLE_ADMIN");

        mockMvc.perform(post("/api/v1/users/1/roles/ROLE_ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Rol no encontrado"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleValidation() throws Exception {
        String invalidRequest = """
        {
          "username": "",
          "password": "123"
        }
        """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(invalidRequest))
                .andExpect(status().isBadRequest()) // ahora sí será 400
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleGeneric() throws Exception {
        Mockito.when(userService.getAllUsers())
                .thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Error inesperado")));
    }
}