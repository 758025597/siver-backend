package com.refricentro.siver.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Lo que SALE por la API al consultar una orden de servicio. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenServicioResponse {

    private Integer id;
    private String numeroOrden;
    private Integer idCliente;
    private Integer idUsuario;
    private Integer idEstado;
    /** Nombre del estado: se puede mostrar sin otra consulta. */
    private String nombreEstado;
    private String equipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String descripcionFalla;
    private String diagnostico;
    private String trabajoRealizado;
    private LocalDateTime fechaIngreso;
    private LocalDate fechaEntregaEstimada;
    private LocalDateTime fechaEntregaReal;
    private BigDecimal costoEstimado;
    private BigDecimal costoFinal;
}