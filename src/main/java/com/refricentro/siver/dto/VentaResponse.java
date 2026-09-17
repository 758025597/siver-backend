package com.refricentro.siver.dto;

import com.refricentro.siver.modelos.Venta;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * La venta como sale por la API, con sus lineas incluidas.
 *
 * subtotal, igv y total vienen calculados por los triggers de MySQL,
 * no por la aplicacion.
 */
public record VentaResponse(
        Integer id,
        String numeroComprobante,
        Venta.TipoComprobante tipoComprobante,
        Integer idCliente,
        Integer idUsuario,
        LocalDateTime fechaVenta,
        Venta.MetodoPago metodoPago,
        BigDecimal subtotal,
        BigDecimal igv,
        BigDecimal total,
        Venta.EstadoVenta estado,
        String observacion,
        List<DetalleVentaResponse> detalles) {
}
