
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
 * Una linea de la venta. Viaja dentro de un VentaRequest.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequest {

    @Schema(description = "Identificador del producto vendido", example = "1")
    @NotNull(message = "El producto es obligatorio")
    private Integer idProducto;

    @Schema(description = "Cantidad de unidades del producto", example = "2")
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @Schema(description = "Precio unitario del producto", example = "150.00")
    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a cero")
    private BigDecimal precioUnitario;

    @Schema(description = "Descuento aplicado a la linea", example = "10.00")
    @PositiveOrZero(message = "El descuento no puede ser negativo")
    private BigDecimal descuento;

    /**
     * Si no mandan descuento, es cero.
     */
    public BigDecimal descuentoOCero() {
        return descuento == null ? BigDecimal.ZERO : descuento;
    }

    /**
     * Valida que el descuento no supere el importe de la linea.
     */
    @JsonIgnore
    @AssertTrue(message = "El descuento no puede ser mayor al importe de la línea")
    public boolean isDescuentoValido() {
        if (cantidad == null || precioUnitario == null) {
            return true;
        }

        BigDecimal importe = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return descuentoOCero().compareTo(importe) <= 0;
    }
}
