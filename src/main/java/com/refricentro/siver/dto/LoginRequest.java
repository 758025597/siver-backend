package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Lo que manda el usuario para iniciar sesion. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

        @Schema(description = "Correo electrónico registrado del usuario", example = "usuario@refricentro.com")
        @NotBlank(message = "{login.correo.notblank}")
        @Email(message = "{login.correo.email}")
        private String correo;

        @Schema(description = "Clave de acceso del usuario", example = "Clave123")
        @NotBlank(message = "{login.clave.notblank}")
        private String clave;
}