package com.security.practice.users.controller;

import com.security.practice.users.config.SecurityConfig;
import com.security.practice.users.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void loginSuccess() throws Exception {
        String request = """
            {
              "username": "andres",
              "password": "123456"
            }
            """;

        Authentication auth = new UsernamePasswordAuthenticationToken("andres", "123456");
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(auth);
        Mockito.when(jwtUtil.generateToken(auth)).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token")); // tu controlador devuelve String plano
    }

    @Test
    void loginFailure() throws Exception {
        String request = """
            {
              "username": "andres",
              "password": "wrongpass"
            }
            """;

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error inesperado: Credenciales inválidas"));
    }
}