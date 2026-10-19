
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

        @NotNull(message = "El rol es obligatorio")
        private Integer idRol;

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
        private String nombres;

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
        private String apellidos;

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
        private String dni;

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 120, message = "El correo no puede pasar de 120 caracteres")
        private String correo;

        @NotBlank(message = "La clave es obligatoria")
        @Size(min = 6, max = 72, message = "La clave debe tener entre 6 y 72 caracteres")
        private String clave;

        @Pattern(regexp = "9\\d{8}", message = "El celular debe tener 9 digitos y empezar en 9")
        private String telefono;
}
