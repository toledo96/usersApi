package com.security.practice.users.service;

import com.security.practice.users.dto.mapper.UserMapper;
import com.security.practice.users.dto.request.UserRequest;
import com.security.practice.users.dto.response.UserResponse;
import com.security.practice.users.exception.RolNotFoundException;
import com.security.practice.users.model.Rol;
import com.security.practice.users.model.User;
import com.security.practice.users.model.UserRole;
import com.security.practice.users.repository.RolRepository;
import com.security.practice.users.repository.UserRepository;
import com.security.practice.users.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest dto) {
        User user = UserMapper.toEntity(dto);
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        userRepository.deleteById(id);

    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> list = userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
        return list;
    }

    @Override
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow();
        Rol rol = rolRepository.findByRoleName(roleName);
        if (rol == null) {
            throw new RolNotFoundException("Rol no encontrado " + roleName);
        }
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRol(rol);
        userRole.setFechaAsignacion(LocalDate.now());

        userRoleRepository.save(userRole);

        user.getUserRoles().add(userRole);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow();
        Rol rol = rolRepository.findByRoleName(roleName);
        if (rol == null) {
            throw new RolNotFoundException("Rol no encontrado: " + roleName);
        }

        // Busco la relación UserRole correspondiente
        UserRole userRoleToRemove = user.getUserRoles().stream()
                .filter(role -> role.getRol().getRoleName().equals(roleName)).findFirst().orElseThrow();

        // Remuevo de la colección y de la BD
        user.getUserRoles().remove(userRoleToRemove);
        userRoleRepository.delete(userRoleToRemove);

        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    @Override
    public List<String> getUserRoles(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        // Converto a lista de nombres de rol
        List<String> roles = user.getUserRoles().stream().map(rol -> rol.getRol().getRoleName()).collect(Collectors.toList());
        return roles;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

//    @Transactional(readOnly = true)
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
//        }
//
//        // Convertir roles a GrantedAuthority usando List
//        List<GrantedAuthority> authorities = user.getUserRoles().stream()
//                .map(userRole -> new SimpleGrantedAuthority(userRole.getRol().getRoleName()))
//                .collect(Collectors.toList());
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                authorities
//        );
//    }
}
