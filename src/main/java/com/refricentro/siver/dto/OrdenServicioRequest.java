package com.refricentro.siver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenServicioRequest {

        @NotBlank(message = "{ordenServicio.numeroOrden.notblank}")
        @Size(max = 20, message = "{ordenServicio.numeroOrden.size}")
        private String numeroOrden;

        @NotNull(message = "{ordenServicio.idCliente.notnull}")
        private Integer idCliente;

        /** Técnico responsable de la atención. */
        @NotNull(message = "{ordenServicio.idUsuario.notnull}")
        private Integer idUsuario;

        /** Solo al crear. En el update el estado se cambia con PATCH /estado. */
        private Integer idEstado;

        @NotBlank(message = "{ordenServicio.equipo.notblank}")
        @Size(min = 3, max = 100, message = "{ordenServicio.equipo.size}")
        private String equipo;

        @Size(max = 60, message = "{ordenServicio.marca.size}")
        private String marca;

        @Size(max = 60, message = "{ordenServicio.modelo.size}")
        private String modelo;

        @Size(max = 60, message = "{ordenServicio.numeroSerie.size}")
        private String numeroSerie;

        @NotBlank(message = "{ordenServicio.descripcionFalla.notblank}")
        @Size(min = 10, message = "{ordenServicio.descripcionFalla.size}")
        private String descripcionFalla;

        /** Obligatorio para cerrar la orden: el trigger lo exige. */
        private String diagnostico;

        private String trabajoRealizado;

        private LocalDate fechaEntregaEstimada;

        @NotNull(message = "{ordenServicio.costoEstimado.notnull}")
        @PositiveOrZero(message = "{ordenServicio.costoEstimado.positiveorzero}")
        private BigDecimal costoEstimado;

        /** Obligatorio para cerrar la orden: el trigger lo exige. */
        @PositiveOrZero(message = "{ordenServicio.costoFinal.positiveorzero}")
        private BigDecimal costoFinal;
}