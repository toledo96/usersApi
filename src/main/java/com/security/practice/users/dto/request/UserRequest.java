package com.security.practice.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "Se debe agregar el username")
    private String username;

    @NotBlank(message = "Se debe agregar el password")
    private String password;

}
