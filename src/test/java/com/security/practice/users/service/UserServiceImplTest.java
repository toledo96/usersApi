package com.security.practice.users.service;

import com.security.practice.users.dto.request.UserRequest;
import com.security.practice.users.dto.response.UserResponse;
import com.security.practice.users.model.Rol;
import com.security.practice.users.model.User;
import com.security.practice.users.model.UserRole;
import com.security.practice.users.repository.RolRepository;
import com.security.practice.users.repository.UserRepository;
import com.security.practice.users.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user = new User();

    private Rol rol = new Rol();

    @BeforeEach
    void setUp() {
        List<UserRole> userRoles = new ArrayList<>();

        user.setId(1L);
        user.setUsername("tj");
        user.setPassword("encodedPassword");

        rol.setRoleName("ROLE_ADMIN");

        userRoles.add(new UserRole(1L,user,rol,LocalDate.now()));

        user.setUserRoles(userRoles);

    }

    @Test
    void createUser() {

        // GIVEN
        Mockito.when(passwordEncoder.encode("renderizer01"))
                        .thenReturn("encodedPassword");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        UserRequest userRequest = UserRequest.builder()
                .username("tj")
                .password("renderizer01")
                .build();
        UserResponse userResponse = UserResponse.builder()
                .username("tj")
                .build();

        // ACTION
        UserResponse response = userService.createUser(userRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals("tj", response.getUsername());

        // VERIFY
        Mockito.verify(passwordEncoder).encode("renderizer01");
        Mockito.verify(userRepository).save(Mockito.any());

    }

    @Test
    void updateUser() {
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(passwordEncoder.encode("renderizer01"))
                .thenReturn("encodedPassword");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(userOptional);

        UserRequest userRequest = UserRequest.builder()
                .username("TJ")
                .password("renderizer01")
                .build();

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(userOptional.get());

        UserResponse response = userService.updateUser(1L,userRequest);

        assertNotNull(response);
        assertEquals("TJ",response.getUsername());

        Mockito.verify(passwordEncoder).encode("renderizer01");
        Mockito.verify(userRepository).findById(Mockito.anyLong());
    }

    @Test
    void updateUser_userNotFound() {
        // GIVEN: simulamos que el repositorio NO encuentra al usuario con ID 1
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Creamos un DTO con los datos que se intentarían actualizar
        UserRequest userRequest = UserRequest.builder()
                .username("TJ")
                .password("renderizer01")
                .build();

        // ACTION + ASSERT:
        // Ejecutamos el método y verificamos que lanza UsernameNotFoundException
        assertThrows(UsernameNotFoundException.class,
                () -> userService.updateUser(1L, userRequest));

        // VERIFY:
        // Confirmamos que se llamó a findById con el ID correcto
        Mockito.verify(userRepository).findById(1L);

        // Confirmamos que NO hubo interacción con el PasswordEncoder
        // porque nunca se llegó a actualizar la contraseña
        Mockito.verifyNoInteractions(passwordEncoder);

        // Confirmamos que NUNCA se intentó guardar un usuario
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteUser() {
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(userOptional);

        userService.deleteUser(1L);

        Mockito.verify(userRepository).findById(Mockito.anyLong());
        Mockito.verify(userRepository).deleteById(Mockito.anyLong());
    }

    @Test
    void getUserById() {
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(userOptional);

        UserResponse user1 = userService.getUserById(1L);

        assertEquals("tj",user1.getUsername());

        Mockito.verify(userRepository).findById(Mockito.anyLong());
    }

    @Test
    void getAllUsers() {
        // GIVEN
        User user = User.builder()
                .id(1L)
                .username("tj")
                .password("encodedPassword")
                .build();
        List<User> users = List.of(user);

        Mockito.when(userRepository.findAll()).thenReturn(users);

        // ACTION
        List<UserResponse> response = userService.getAllUsers();

        // ASSERT
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("tj", response.get(0).getUsername());

        // VERIFY
        Mockito.verify(userRepository).findAll();

    }

    @Test
    void assignRoleToUser() {
        Optional<User> userOptional = Optional.of(user);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(userOptional);
        Mockito.when(rolRepository.findByRoleName(Mockito.anyString())).thenReturn(rol);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRol(rol);
        userRole.setFechaAsignacion(LocalDate.now());

        Mockito.when(userRoleRepository.save(Mockito.any())).thenReturn(userRole);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        userService.assignRoleToUser(1L,rol.getRoleName());

        Mockito.verify(userRepository).findById(Mockito.anyLong());
        Mockito.verify(rolRepository).findByRoleName(Mockito.anyString());
        Mockito.verify(userRoleRepository).save(Mockito.any());
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void removeRoleFromUser() {
        Optional<User> userOptional = Optional.of(user);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(userOptional);
        Mockito.when(rolRepository.findByRoleName(Mockito.anyString())).thenReturn(rol);

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        userService.removeRoleFromUser(1L,user.getUserRoles().get(0).getRol().getRoleName());

        Mockito.verify(userRepository).findById(Mockito.anyLong());
        Mockito.verify(rolRepository).findByRoleName(Mockito.anyString());
        Mockito.verify(userRoleRepository).delete(Mockito.any());
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    void getUserRoles() {
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(userOptional);

        List<String> roles = userService.getUserRoles(1L);

        assertEquals("ROLE_ADMIN",roles.get(0));

        Mockito.verify(userRepository).findById(Mockito.anyLong());
    }

    @Test
    void existsByUsername() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);

        boolean respond = userService.existsByUsername("tj");

        assertEquals(true,respond);

        Mockito.verify(userRepository).findByUsername(Mockito.anyString());

    }

    @Test
    void loadUserByUsername() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);

        UserDetails userDetails =  customUserDetailsService.loadUserByUsername("tj");

        assertEquals("tj",userDetails.getUsername());

        Mockito.verify(userRepository).findByUsername(Mockito.anyString());
    }
}