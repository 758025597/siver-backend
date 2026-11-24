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
 * Lo que ENTRA por la API al crear o actualizar un estado.
 *
 * Reflejo de la tabla estado_orden:
 *   nombre       VARCHAR(50) NOT NULL UNIQUE
 *   descripcion  VARCHAR(200) NULL
 *   orden_flujo  TINYINT NOT NULL UNIQUE
 *   ck_estado_flujo  ->  orden_flujo > 0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoOrdenRequest {

        @Schema(description = "Nombre del estado de la orden de servicio", example = "En reparación")
        @NotBlank(message = "{estadoOrden.nombre.notblank}")
        @Size(max = 50, message = "{estadoOrden.nombre.size}")
        private String nombre;

        @Schema(description = "Descripción del estado", example = "El equipo se encuentra en proceso de reparación")
        @Size(max = 200, message = "{estadoOrden.descripcion.size}")
        private String descripcion;

        @Schema(description = "Posición del estado dentro del flujo de servicio", example = "3")
        /** Posición dentro del flujo: 1 Recibido, 2 En revisión, 3 En reparación... */
        @NotNull(message = "{estadoOrden.ordenFlujo.notnull}")
        @Positive(message = "{estadoOrden.ordenFlujo.positive}")
        private Byte ordenFlujo;

        @Schema(description = "Indica si el estado es final y cierra la orden", example = "false")
        /** true si es un estado que cierra la orden: Entregado o Cancelado. */
        @NotNull(message = "{estadoOrden.esFinal.notnull}")
        private Boolean esFinal;
}