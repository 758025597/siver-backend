
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
        @NotBlank(message = "{rol.nombre.notblank}")
        @Size(min = 3, max = 50, message = "{rol.nombre.size}")
        private String nombre;

        @Schema(description = "Descripción del rol", example = "Usuario con permisos administrativos")
        @Size(max = 200, message = "{rol.descripcion.size}")
        private String descripcion;
}
