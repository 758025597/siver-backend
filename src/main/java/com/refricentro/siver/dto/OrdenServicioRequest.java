package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(description = "Número único de la orden de servicio", example = "OS-00001")
        @NotBlank(message = "{ordenServicio.numeroOrden.notblank}")
        @Size(max = 20, message = "{ordenServicio.numeroOrden.size}")
        private String numeroOrden;

        @Schema(description = "Identificador del cliente que solicita el servicio", example = "1")
        @NotNull(message = "{ordenServicio.idCliente.notnull}")
        private Integer idCliente;

        @Schema(description = "Identificador del técnico responsable de la atención", example = "2")
        /** Técnico responsable de la atención. */
        @NotNull(message = "{ordenServicio.idUsuario.notnull}")
        private Integer idUsuario;

        @Schema(description = "Identificador del estado inicial de la orden. Solo se utiliza al crearla", example = "1")
        /** Solo al crear. En el update el estado se cambia con PATCH /estado. */
        private Integer idEstado;

        @Schema(description = "Nombre o descripción del equipo recibido", example = "Refrigeradora")
        @NotBlank(message = "{ordenServicio.equipo.notblank}")
        @Size(min = 3, max = 100, message = "{ordenServicio.equipo.size}")
        private String equipo;

        @Schema(description = "Marca del equipo", example = "Samsung")
        @Size(max = 60, message = "{ordenServicio.marca.size}")
        private String marca;

        @Schema(description = "Modelo del equipo", example = "RT38K5930SL")
        @Size(max = 60, message = "{ordenServicio.modelo.size}")
        private String modelo;

        @Schema(description = "Número de serie del equipo", example = "SN123456789")
        @Size(max = 60, message = "{ordenServicio.numeroSerie.size}")
        private String numeroSerie;

        @Schema(description = "Descripción detallada de la falla reportada", example = "La refrigeradora no enfría correctamente")
        @NotBlank(message = "{ordenServicio.descripcionFalla.notblank}")
        @Size(min = 10, message = "{ordenServicio.descripcionFalla.size}")
        private String descripcionFalla;

        @Schema(description = "Diagnóstico técnico del equipo", example = "Falla en el compresor")
        /** Obligatorio para cerrar la orden: el trigger lo exige. */
        private String diagnostico;

        @Schema(description = "Descripción del trabajo realizado", example = "Se reemplazó el compresor y se verificó el funcionamiento")
        private String trabajoRealizado;

        @Schema(description = "Fecha estimada de entrega del equipo", example = "2026-11-15")
        private LocalDate fechaEntregaEstimada;

        @Schema(description = "Costo estimado del servicio", example = "250.00")
        @NotNull(message = "{ordenServicio.costoEstimado.notnull}")
        @PositiveOrZero(message = "{ordenServicio.costoEstimado.positiveorzero}")
        private BigDecimal costoEstimado;

        @Schema(description = "Costo final del servicio", example = "280.00")
        /** Obligatorio para cerrar la orden: el trigger lo exige. */
        @PositiveOrZero(message = "{ordenServicio.costoFinal.positiveorzero}")
        private BigDecimal costoFinal;
}