package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * Una linea de la venta. Viaja SIEMPRE dentro de un VentaRequest:
 * no existe un endpoint para crear lineas sueltas.
 *
 * Reflejo de los CHECK de detalle_venta:
 *   ck_detalle_cantidad  ->  cantidad > 0
 *   ck_detalle_precio    ->  precio_unitario > 0
 *   ck_detalle_descuento ->  descuento >= 0 AND descuento <= cantidad * precio_unitario
 *
 * No se pide el subtotal: lo calcula trg_detalle_venta_before_insert.
 */
public record DetalleVentaRequest(

        @NotNull(message = "El producto es obligatorio")
        Integer idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        @Positive(message = "El precio unitario debe ser mayor a cero")
        BigDecimal precioUnitario,

        @PositiveOrZero(message = "El descuento no puede ser negativo")
        BigDecimal descuento) {

    /** Si no mandan descuento, es cero. */
    public BigDecimal descuentoOCero() {
        return descuento == null ? BigDecimal.ZERO : descuento;
    }

    /** Valida ck_detalle_descuento: el descuento no puede superar el importe. */
    @JsonIgnore
    @AssertTrue(message = "El descuento no puede ser mayor al importe de la línea")
    public boolean isDescuentoValido() {
        if (cantidad == null || precioUnitario == null) {
            return true; // ya lo reportan los @NotNull
        }
        BigDecimal importe = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return descuentoOCero().compareTo(importe) <= 0;
    }
}
