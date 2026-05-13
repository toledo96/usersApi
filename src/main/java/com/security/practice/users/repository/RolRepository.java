package com.security.practice.users.repository;

import com.security.practice.users.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol,Long> {
    Rol findByRoleName(String roleName);
}
