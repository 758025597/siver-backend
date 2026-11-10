package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequest {

    @Schema(description = "Identificador del producto vendido", example = "1")
    @NotNull(message = "{detalleVenta.idProducto.notnull}")
    private Integer idProducto;

    @Schema(description = "Cantidad de unidades del producto", example = "2")
    @NotNull(message = "{detalleVenta.cantidad.notnull}")
    @Positive(message = "{detalleVenta.cantidad.positive}")
    private Integer cantidad;

    @Schema(description = "Precio unitario del producto", example = "150.00")
    @NotNull(message = "{detalleVenta.precioUnitario.notnull}")
    @Positive(message = "{detalleVenta.precioUnitario.positive}")
    private BigDecimal precioUnitario;

    @Schema(description = "Descuento aplicado a la linea", example = "10.00")
    @PositiveOrZero(message = "{detalleVenta.descuento.positiveorzero}")
    private BigDecimal descuento;

    /** Si no mandan descuento, es cero. */
    public BigDecimal descuentoOCero() {
        return descuento == null ? BigDecimal.ZERO : descuento;
    }

    /** Valida ck_detalle_descuento: el descuento no puede superar el importe. */
    @JsonIgnore
    @AssertTrue(message = "{detalleVenta.descuentoValido.asserttrue}")
    public boolean isDescuentoValido() {
        if (cantidad == null || precioUnitario == null) {
            return true; // ya lo reportan los @NotNull
        }
        BigDecimal importe = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return descuentoOCero().compareTo(importe) <= 0;
    }
}