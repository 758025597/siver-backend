
package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo que entra por la API al crear o actualizar un rol.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolRequest {

        @Schema(description = "Nombre del rol", example = "Administrador")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        private String nombre;

        @Schema(description = "Descripción del rol", example = "Usuario con permisos administrativos")
        @Size(max = 200, message = "La descripcion no puede pasar de 200 caracteres")
        private String descripcion;
}
