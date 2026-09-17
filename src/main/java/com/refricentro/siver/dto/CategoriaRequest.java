package com.refricentro.siver.dto;

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

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String descripcion;
}
