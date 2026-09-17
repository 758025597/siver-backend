package com.refricentro.siver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lo que ENTRA por la API al registrar o actualizar una orden de servicio.
 *
 * Reflejo de los CHECK de la tabla orden_servicio:
 *   ck_orden_equipo          ->  CHAR_LENGTH(TRIM(equipo)) >= 3
 *   ck_orden_falla           ->  CHAR_LENGTH(TRIM(descripcion_falla)) >= 10
 *   ck_orden_costo_estimado  ->  costo_estimado >= 0
 *   ck_orden_costo_final     ->  costo_final IS NULL OR costo_final >= 0
 *
 * No se piden aqui: fecha_ingreso (la pone MySQL), fecha_entrega_real (la
 * pone el trigger al cerrar) ni id_estado (se mueve con PATCH /estado).
 */
public record OrdenServicioRequest(

        @NotBlank(message = "El número de orden es obligatorio")
        @Size(max = 20, message = "El número de orden no puede superar los 20 caracteres")
        String numeroOrden,

        @NotNull(message = "El cliente es obligatorio")
        Integer idCliente,

        /** Técnico responsable de la atención. */
        @NotNull(message = "El técnico es obligatorio")
        Integer idUsuario,

        /** Solo al crear. En el update el estado se cambia con PATCH /estado. */
        Integer idEstado,

        @NotBlank(message = "El equipo es obligatorio")
        @Size(min = 3, max = 100, message = "El equipo debe tener entre 3 y 100 caracteres")
        String equipo,

        @Size(max = 60, message = "La marca no puede superar los 60 caracteres")
        String marca,

        @Size(max = 60, message = "El modelo no puede superar los 60 caracteres")
        String modelo,

        @Size(max = 60, message = "El número de serie no puede superar los 60 caracteres")
        String numeroSerie,

        @NotBlank(message = "La descripción de la falla es obligatoria")
        @Size(min = 10, message = "Describe la falla con al menos 10 caracteres")
        String descripcionFalla,

        /** Obligatorio para cerrar la orden: el trigger lo exige. */
        String diagnostico,

        String trabajoRealizado,

        LocalDate fechaEntregaEstimada,

        @NotNull(message = "El costo estimado es obligatorio")
        @PositiveOrZero(message = "El costo estimado no puede ser negativo")
        BigDecimal costoEstimado,

        /** Obligatorio para cerrar la orden: el trigger lo exige. */
        @PositiveOrZero(message = "El costo final no puede ser negativo")
        BigDecimal costoFinal) {
}
