package com.security.practice.users.controller;

import com.security.practice.users.config.SecurityConfig;
import com.security.practice.users.dto.response.UserResponse;
import com.security.practice.users.model.Rol;
import com.security.practice.users.model.User;
import com.security.practice.users.service.UserService;
import com.security.practice.users.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtUtil.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private User user = new User();

    private Rol rol = new Rol();



    @Test
    void createUser() throws Exception {
        String request = """
                {
                  "username": "andres",
                  "password": "123456",
                  "roles": ["ROLE_ADMIN"]
                }
                """;

        String response = """
                {
                    "username": "andres"
                }
                """;

        UserResponse userResponse = UserResponse.builder()
                .username("andres")
                .build();

        Mockito.when(userService.createUser(Mockito.any()))
                .thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/users")
                .contentType("application/json")
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(content().json(response));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser() throws Exception {
        String request = """
                {
                  "id": 1,
                  "username": "andres",
                  "password": "123456",
                  "roles": ["ROLE_ADMIN"]
                }
                """;


        UserResponse userResponse = UserResponse.builder()
                .username("andres123")
                .build();

        Mockito.when(userService.updateUser(Mockito.anyLong(),Mockito.any()))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/users/1")
                .contentType("application/json")
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("andres123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")
                .contentType("application/json"))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById() throws Exception {
        UserResponse response = UserResponse.builder()
                .username("andres123")
                .build();

        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("andres123"));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers() throws Exception {
        List<UserResponse> userResponses = new ArrayList<>();
        UserResponse response = UserResponse.builder()
                .username("andres123")
                .build();
        userResponses.add(response);

        Mockito.when(userService.getAllUsers()).thenReturn(userResponses);
        mockMvc.perform(get("/api/v1/users")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("andres123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignRoleToUser() throws Exception {

        Long userId = 1L;
        String roleName = "ROLE_ADMIN";

        Mockito.doNothing().when(userService).assignRoleToUser(userId,roleName);

        mockMvc.perform(post("/api/v1/users/{userId}/roles/{roleName}", userId, roleName)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeRoleFromUser() throws Exception {

        Long userId = 1L;
        String roleName = "ROLE_ADMIN";

        Mockito.doNothing().when(userService).removeRoleFromUser(userId,roleName);

        mockMvc.perform(post("/api/v1/users/{userId}/roles/{roleName}", userId, roleName)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserRoles() throws Exception {
        Long userId = 1L;
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");

        Mockito.when(userService.getUserRoles(userId)).thenReturn(roles);

        mockMvc.perform(get("/api/v1/users/{userId}/roles", userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$[1]").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void existsByUsername() throws Exception {
        String username = "andres123";

        Mockito.when(userService.existsByUsername(username)).thenReturn(true);

        mockMvc.perform(get("/api/v1/users/exists/{username}", username)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); // valida que el body es "true"
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/users/admin-only")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("Solo admins pueden ver esto"));
    }

}