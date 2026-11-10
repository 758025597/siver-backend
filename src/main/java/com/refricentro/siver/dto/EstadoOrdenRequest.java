
package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo que entra por la API al crear o actualizar un estado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoOrdenRequest {

        @Schema(description = "Nombre del estado de la orden de servicio", example = "En reparación")
        @NotBlank(message = "El nombre del estado es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        private String nombre;

        @Schema(description = "Descripción del estado", example = "El equipo se encuentra en proceso de reparación")
        @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
        private String descripcion;

        @Schema(description = "Posición del estado dentro del flujo de servicio", example = "3")
        @NotNull(message = "El orden de flujo es obligatorio")
        @Positive(message = "El orden de flujo debe ser mayor a cero")
        private Byte ordenFlujo;

        @Schema(description = "Indica si el estado es final y cierra la orden", example = "false")
        @NotNull(message = "Debe indicar si es un estado final")
        private Boolean esFinal;
}
