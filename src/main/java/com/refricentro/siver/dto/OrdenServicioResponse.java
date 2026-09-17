package com.refricentro.siver.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Lo que SALE por la API al consultar una orden de servicio. */
public record OrdenServicioResponse(
        Integer id,
        String numeroOrden,
        Integer idCliente,
        Integer idUsuario,
        Integer idEstado,
        /** Nombre del estado: se puede mostrar sin otra consulta. */
        String nombreEstado,
        String equipo,
        String marca,
        String modelo,
        String numeroSerie,
        String descripcionFalla,
        String diagnostico,
        String trabajoRealizado,
        LocalDateTime fechaIngreso,
        LocalDate fechaEntregaEstimada,
        LocalDateTime fechaEntregaReal,
        BigDecimal costoEstimado,
        BigDecimal costoFinal) {
}
