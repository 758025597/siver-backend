package com.refricentro.siver.dto;

import java.math.BigDecimal;

/** Una linea de la venta tal como sale por la API. */
public record DetalleVentaResponse(
        Integer id,
        Integer idProducto,
        /** Nombre del producto: evita otra consulta desde el frontend. */
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal descuento,
        /** Lo calculo el trigger: (cantidad * precio) - descuento. */
        BigDecimal subtotal) {
}
