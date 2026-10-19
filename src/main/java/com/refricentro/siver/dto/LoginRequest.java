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

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        private String correo;

        @NotBlank(message = "La clave es obligatoria")
        private String clave;
}