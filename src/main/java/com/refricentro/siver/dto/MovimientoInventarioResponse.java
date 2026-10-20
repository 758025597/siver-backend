package com.refricentro.siver.dto;

import com.refricentro.siver.model.enums.TipoMovimiento;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioResponse {

    private Integer idMovimiento;
    private Integer idProducto;
    private Integer idUsuario;
    private Integer idVenta;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private String motivo;
    private LocalDateTime fechaMovimiento;
}