package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Lo que ENTRA por la API al crear o actualizar una categoría.
 *
 * Reflejo exacto de la tabla categoria:
 *   nombre       VARCHAR(80) NOT NULL UNIQUE
 *   descripcion  VARCHAR(200) NULL
 *   ck_categoria_nombre  ->  CHAR_LENGTH(TRIM(nombre)) >= 3
 */
@Data
public class CategoriaRequest {

    @Schema(
            description = "Nombre de la categoría del producto",
            example = "Refrigeradoras"
    )
    @NotBlank(message = "{categoria.nombre.notblank}")
    @Size(min = 3, max = 80, message = "{categoria.nombre.size}")
    private String nombre;

    @Schema(
            description = "Descripción de la categoría",
            example = "Equipos de refrigeración para uso doméstico"
    )
    @Size(max = 200, message = "{categoria.descripcion.size}")
    private String descripcion;
}
