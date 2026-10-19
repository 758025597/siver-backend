package com.refricentro.siver.dto;

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

        @NotBlank(message = "{login.correo.notblank}")
        @Email(message = "{login.correo.email}")
        private String correo;

        @NotBlank(message = "{login.clave.notblank}")
        private String clave;
}