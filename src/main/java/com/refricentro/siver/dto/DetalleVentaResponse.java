package com.refricentro.siver.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Una linea de la venta tal como sale por la API. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaResponse {

    private Integer id;
    private Integer idProducto;
    /** Nombre del producto: evita otra consulta desde el frontend. */
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    /** Lo calculo el trigger: (cantidad * precio) - descuento. */
    private BigDecimal subtotal;
}