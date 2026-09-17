package com.refricentro.siver.dto;

import com.refricentro.siver.modelos.enums.TipoMovimiento;
import java.time.LocalDateTime;

public record MovimientoInventarioResponse(
        Integer idMovimiento,
        Integer idProducto,
        Integer idUsuario,
        Integer idVenta,
        TipoMovimiento tipoMovimiento,
        Integer cantidad,
        Integer stockAnterior,
        Integer stockNuevo,
        String motivo,
        LocalDateTime fechaMovimiento
) {
}