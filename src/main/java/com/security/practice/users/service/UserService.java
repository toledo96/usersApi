package com.security.practice.users.service;

import com.security.practice.users.dto.request.UserRequest;
import com.security.practice.users.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserResponse createUser(UserRequest dto);
    UserResponse updateUser(Long id, UserRequest dto);
    void deleteUser(Long id);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();

    void assignRoleToUser(Long userId, String roleName);
    void removeRoleFromUser(Long userId, String roleName);
    List<String> getUserRoles(Long userId);

    boolean existsByUsername(String username);
}
