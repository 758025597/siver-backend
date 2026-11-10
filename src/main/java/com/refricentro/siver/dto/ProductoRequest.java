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

    @Schema(description = "Código único de identificación del producto", example = "REF-001")
    @NotBlank(message = "{producto.codigo.notblank}")
    @Size(max = 30, message = "{producto.codigo.size}")
    private String codigo;

    @Schema(description = "Nombre del producto", example = "Refrigeradora Samsung")
    @NotBlank(message = "{producto.nombre.notblank}")
    @Size(min = 3, max = 150, message = "{producto.nombre.size}")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Refrigeradora de dos puertas, capacidad de 300 litros")
    @Size(max = 300, message = "{producto.descripcion.size}")
    private String descripcion;

    @Schema(description = "Identificador de la categoría del producto", example = "1")
    @NotNull(message = "{producto.idCategoria.notnull}")
    private Integer idCategoria;

    @Schema(description = "Identificador del proveedor del producto (opcional)", example = "2")
    /** Opcional: la columna id_proveedor admite NULL. */
    private Integer idProveedor;

    @Schema(description = "Unidad de medida utilizada para el producto", example = "UNIDAD")
    @NotNull(message = "{producto.unidadMedida.notnull}")
    private UnidadMedida unidadMedida;

    @Schema(description = "Precio de compra del producto", example = "850.00")
    @NotNull(message = "{producto.precioCompra.notnull}")
    @PositiveOrZero(message = "{producto.precioCompra.positiveorzero}")
    private BigDecimal precioCompra;

    @Schema(description = "Precio de venta del producto", example = "1200.00")
    @NotNull(message = "{producto.precioVenta.notnull}")
    @Positive(message = "{producto.precioVenta.positive}")
    private BigDecimal precioVenta;

    /**
     * Stock INICIAL. Solo se usa al crear el producto.
     * Al actualizar se ignora, porque el stock lo mantienen los triggers.
     * Para ajustarlo se registra un movimiento de inventario.
     */
    @Schema(description = "Stock inicial del producto al momento de crearlo", example = "15")
    @NotNull(message = "{producto.stock.notnull}")
    @PositiveOrZero(message = "{producto.stock.positiveorzero}")
    private Integer stock;

    @Schema(description = "Cantidad mínima de unidades que debe mantenerse en inventario", example = "3")
    @NotNull(message = "{producto.stockMinimo.notnull}")
    @PositiveOrZero(message = "{producto.stockMinimo.positiveorzero}")
    private Integer stockMinimo;

    /**
     * Valida ck_producto_margen: no se puede vender por debajo del costo.
     *
     * Es una regla que compara DOS campos entre si, y un @Positive o un
     * @Min no llegan ahi. Para esos casos Bean Validation ofrece @AssertTrue.
     */
    @JsonIgnore
    @AssertTrue(message = "{producto.margenValido.asserttrue}")
    public boolean isMargenValido() {
        // Si falta alguno, ya lo reportan los @NotNull de arriba.
        if (precioCompra == null || precioVenta == null) {
            return true;
        }
        return precioVenta.compareTo(precioCompra) >= 0;
    }
}
