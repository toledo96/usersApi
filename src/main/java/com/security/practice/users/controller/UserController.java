package com.security.practice.users.controller;

import com.security.practice.users.dto.request.UserRequest;
import com.security.practice.users.dto.response.UserResponse;
import com.security.practice.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Operaciones sobre usuarios")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Crear un usuario", description = "Crea un usuario a través del modelo User")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @Operation(summary = "Editar un usuario", description = "Edita un usuario a través del modelo User")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser( @PathVariable Long id,
                                                   @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }


    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario a través del modelo User")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener usuario por ID
    @Operation(summary = "Obtener un usuario", description = "Obtener un usuario a través del ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Listar todos los usuarios
    @Operation(summary = "Listar usuarios", description = "Enlista los usuarios")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Asignar rol a usuario
    @Operation(summary = "Asignar rol a usuario", description = "Asigna un rol existente a un usuario por ID")
    @PostMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> assignRoleToUser( @PathVariable Long userId, @PathVariable String roleName) {
        userService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok().build();
    }

    // Remover rol de usuario
    @Operation(summary = "Eliminar rol de un usuario", description = "Edita un usuario a través del modelo User")
    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName) {
        userService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok().build();
    }

    // Obtener roles de un usuario
    @Operation(summary = "Obtener roles de usuario", description = "Obtiene los roles de un usuario")
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserRoles(userId));
    }

    // Verificar existencia por username
    @Operation(summary = "Verificar existencia de usuario", description = "Verifica la existencia de un usuario")
    @GetMapping("/exists/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.existsByUsername(username));
    }

    // Método para que el admin solo pueda ejecutar
    @Operation(summary = "endpoint para admin", description = "Solo usuarios con rol de admin pueden consumir este endpoint")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-only")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Solo admins pueden ver esto");
    }


}
