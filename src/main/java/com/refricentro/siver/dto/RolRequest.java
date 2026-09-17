package com.refricentro.siver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Lo que ENTRA por la API al crear o actualizar un rol.
 *
 * Las validaciones son el reflejo de las restricciones de la tabla:
 *  - ck_rol_nombre: CHAR_LENGTH(TRIM(nombre)) >= 3
 *  - nombre VARCHAR(50) NOT NULL
 *  - descripcion VARCHAR(200) NULL
 *
 * Se validan aqui para devolver un 400 con un mensaje claro, en vez de
 * dejar que reviente MySQL y salga un error de constraint.
 */
public record RolRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripcion no puede pasar de 200 caracteres")
        String descripcion) {
}
