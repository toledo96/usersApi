package com.security.practice.users.config;

import com.security.practice.users.model.Rol;
import com.security.practice.users.model.User;
import com.security.practice.users.model.UserRole;
import com.security.practice.users.repository.RolRepository;
import com.security.practice.users.repository.UserRepository;
import com.security.practice.users.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initData(RolRepository rolRepository,
                               UserRepository userRepository,
                               UserRoleRepository userRoleRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear roles si no existen
            if (rolRepository.findByRoleName("ROLE_ADMIN") == null) {
                rolRepository.save(Rol.builder().roleName("ROLE_ADMIN").build());
            }
            if (rolRepository.findByRoleName("ROLE_USER") == null) {
                rolRepository.save(Rol.builder().roleName("ROLE_USER").build());
            }

            Rol adminRole = rolRepository.findByRoleName("ROLE_ADMIN");

            // Crear usuario admin inicial si no existe
            if (userRepository.findByUsername(adminUsername) == null) {
                User admin = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .build();
                userRepository.save(admin);

                userRoleRepository.save(UserRole.builder()
                        .user(admin)
                        .rol(adminRole)
                        .fechaAsignacion(LocalDate.now())
                        .build());
            }
        };
    }
}