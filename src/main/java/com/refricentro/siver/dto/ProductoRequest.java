package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.refricentro.siver.modelos.enums.UnidadMedida;
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
 *
 * Reflejo exacto de los CHECK de la tabla producto:
 *   ck_producto_nombre        ->  CHAR_LENGTH(TRIM(nombre)) >= 3
 *   ck_producto_precio_compra ->  precio_compra >= 0
 *   ck_producto_precio_venta  ->  precio_venta > 0
 *   ck_producto_margen        ->  precio_venta >= precio_compra
 *   ck_producto_stock         ->  stock >= 0
 *   ck_producto_stock_minimo  ->  stock_minimo >= 0
 */
@Data
public class ProductoRequest {

    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 30, message = "El código no puede superar los 30 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;

    /** Opcional: la columna id_proveedor admite NULL. */
    private Integer idProveedor;

    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedida unidadMedida;

    @NotNull(message = "El precio de compra es obligatorio")
    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor a cero")
    private BigDecimal precioVenta;

    /**
     * Stock INICIAL. Solo se usa al crear el producto.
     * Al actualizar se ignora, porque el stock lo mantienen los triggers.
     * Para ajustarlo se registra un movimiento de inventario.
     */
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    /**
     * Valida ck_producto_margen: no se puede vender por debajo del costo.
     *
     * Es una regla que compara DOS campos entre si, y un @Positive o un
     * @Min no llegan ahi. Para esos casos Bean Validation ofrece @AssertTrue.
     */
    @JsonIgnore
    @AssertTrue(message = "El precio de venta no puede ser menor al precio de compra")
    public boolean isMargenValido() {
        // Si falta alguno, ya lo reportan los @NotNull de arriba.
        if (precioCompra == null || precioVenta == null) {
            return true;
        }
        return precioVenta.compareTo(precioCompra) >= 0;
    }
}
