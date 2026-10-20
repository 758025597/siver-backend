
package com.refricentro.siver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo que entra por la API al crear o actualizar un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

        @NotNull(message = "{usuario.idRol.notnull}")
        private Integer idRol;

        @NotBlank(message = "{usuario.nombres.notblank}")
        @Size(min = 2, max = 100, message = "{usuario.nombres.size}")
        private String nombres;

        @NotBlank(message = "{usuario.apellidos.notblank}")
        @Size(min = 2, max = 100, message = "{usuario.apellidos.size}")
        private String apellidos;

        @NotBlank(message = "{usuario.dni.notblank}")
        @Pattern(regexp = "\\d{8}", message = "{usuario.dni.pattern}")
        private String dni;

        @NotBlank(message = "{usuario.correo.notblank}")
        @Email(message = "{usuario.correo.email}")
        @Size(max = 120, message = "{usuario.correo.size}")
        private String correo;

        @NotBlank(message = "{usuario.clave.notblank}")
        @Size(min = 6, max = 72, message = "{usuario.clave.size}")
        private String clave;

        @Pattern(regexp = "9\\d{8}", message = "{usuario.telefono.pattern}")
        private String telefono;
}
