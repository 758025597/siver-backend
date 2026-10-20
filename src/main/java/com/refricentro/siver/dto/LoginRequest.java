package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Lo que manda el usuario para iniciar sesion. */
public record LoginRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @JsonAlias({"email", "username", "usuario"})
        String correo,

        @NotBlank(message = "La clave es obligatoria")
        @JsonAlias({"password", "contrasena", "contraseña"})
        String clave) {
}
