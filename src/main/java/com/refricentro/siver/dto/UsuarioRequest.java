
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
        @NotNull(message = "El rol es obligatorio")
        private Integer idRol;

        @Schema(description = "Nombres del usuario", example = "Juan Carlos")
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
        private String nombres;

        @Schema(description = "Apellidos del usuario", example = "Perez Ramirez")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
        private String apellidos;

        @Schema(description = "Documento Nacional de Identidad de 8 digitos", example = "12345678")
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
        private String dni;

        @Schema(description = "Correo electronico del usuario", example = "juan.perez@gmail.com")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 120, message = "El correo no puede pasar de 120 caracteres")
        private String correo;

        @Schema(description = "Clave de acceso del usuario en texto plano", example = "Clave123")
        @NotBlank(message = "La clave es obligatoria")
        @Size(min = 6, max = 72, message = "La clave debe tener entre 6 y 72 caracteres")
        private String clave;

        @Schema(description = "Numero de celular peruano", example = "987654321")
        @Pattern(regexp = "9\\d{8}", message = "El celular debe tener 9 digitos y empezar en 9")
        private String telefono;
}
