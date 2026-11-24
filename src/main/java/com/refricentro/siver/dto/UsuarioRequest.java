
package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(description = "Identificador del rol asignado al usuario", example = "1")
        @NotNull(message = "{usuario.idRol.notnull}")
        private Integer idRol;

        @Schema(description = "Nombres del usuario", example = "Juan Carlos")
        @NotBlank(message = "{usuario.nombres.notblank}")
        @Size(min = 2, max = 100, message = "{usuario.nombres.size}")
        private String nombres;

        @Schema(description = "Apellidos del usuario", example = "Perez Ramirez")
        @NotBlank(message = "{usuario.apellidos.notblank}")
        @Size(min = 2, max = 100, message = "{usuario.apellidos.size}")
        private String apellidos;

        @Schema(description = "Documento Nacional de Identidad de 8 digitos", example = "12345678")
        @NotBlank(message = "{usuario.dni.notblank}")
        @Pattern(regexp = "\\d{8}", message = "{usuario.dni.pattern}")
        private String dni;

        @Schema(description = "Correo electronico del usuario", example = "juan.perez@gmail.com")
        @NotBlank(message = "{usuario.correo.notblank}")
        @Email(message = "{usuario.correo.email}")
        @Size(max = 120, message = "{usuario.correo.size}")
        private String correo;

        @Schema(description = "Clave de acceso del usuario en texto plano", example = "Clave123")
        @NotBlank(message = "{usuario.clave.notblank}")
        @Size(min = 6, max = 72, message = "{usuario.clave.size}")
        private String clave;

        @Schema(description = "Numero de celular peruano", example = "987654321")
        @Pattern(regexp = "9\\d{8}", message = "{usuario.telefono.pattern}")
        private String telefono;
}
