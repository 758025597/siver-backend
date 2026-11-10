
package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.refricentro.siver.model.enums.UnidadMedida;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

/**
 * Lo que ENTRA por la API al crear o actualizar un producto.
 */
@Data
public class ProductoRequest {

    @Schema(description = "Código único de identificación del producto", example = "REF-001")
    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 30, message = "El código no puede superar los 30 caracteres")
    private String codigo;

    @Schema(description = "Nombre del producto", example = "Refrigeradora Samsung")
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Refrigeradora de dos puertas, capacidad de 300 litros")
    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    private String descripcion;

    @Schema(description = "Identificador de la categoría del producto", example = "1")
    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;

    @Schema(description = "Identificador del proveedor del producto (opcional)", example = "2")
    private Integer idProveedor;

    @Schema(description = "Unidad de medida utilizada para el producto", example = "UNIDAD")
    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedida unidadMedida;

    @Schema(description = "Precio de compra del producto", example = "850.00")
    @NotNull(message = "El precio de compra es obligatorio")
    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    private BigDecimal precioCompra;

    @Schema(description = "Precio de venta del producto", example = "1200.00")
    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor a cero")
    private BigDecimal precioVenta;

    @Schema(description = "Stock inicial del producto al momento de crearlo", example = "15")
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Cantidad mínima de unidades que debe mantenerse en inventario", example = "3")
    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    /**
     * Valida que el precio de venta no sea menor al precio de compra.
     */
    @JsonIgnore
    @AssertTrue(message = "El precio de venta no puede ser menor al precio de compra")
    public boolean isMargenValido() {
        if (precioCompra == null || precioVenta == null) {
            return true;
        }
        return precioVenta.compareTo(precioCompra) >= 0;
    }
}
