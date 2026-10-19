package com.refricentro.siver.dto;

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

        @NotBlank(message = "El nombre del estado es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        private String nombre;

        @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
        private String descripcion;

        /** Posición dentro del flujo: 1 Recibido, 2 En revisión, 3 En reparación... */
        @NotNull(message = "El orden de flujo es obligatorio")
        @Positive(message = "El orden de flujo debe ser mayor a cero")
        private Byte ordenFlujo;

        /** true si es un estado que cierra la orden: Entregado o Cancelado. */
        @NotNull(message = "Debe indicar si es un estado final")
        private Boolean esFinal;
}