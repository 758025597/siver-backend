
package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Lo que ENTRA por la API al crear o actualizar una categoría.
 *
 * Reflejo exacto de la tabla categoria.
 */
@Data
public class CategoriaRequest {

    @Schema(
            description = "Nombre de la categoría del producto",
            example = "Refrigeradoras"
    )
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    private String nombre;

    @Schema(
            description = "Descripción de la categoría",
            example = "Equipos de refrigeración para uso doméstico"
    )
    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String descripcion;
}
