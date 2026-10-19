
package com.refricentro.siver.dto;

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

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        private String nombre;

        @Size(max = 200, message = "La descripcion no puede pasar de 200 caracteres")
        private String descripcion;
}
