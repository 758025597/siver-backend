package com.refricentro.siver.dto;

import com.refricentro.siver.model.enums.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Movimiento manual de inventario (solo ADMINISTRADOR).
 *
 *   ENTRADA  llego mercaderia:            stock = stock + cantidad
 *   SALIDA   merma, rotura, uso interno:  stock = stock - cantidad
 *   AJUSTE   conteo fisico:               stock = cantidad (el numero contado)
 *
 * Las salidas por VENTA no se registran aqui: las escribe el trigger de MySQL.
 */
@Data
@Schema(description = "Entrada, salida o ajuste manual de stock")
public class MovimientoInventarioRequest {

    @NotNull(message = "Elige el producto")
    @Schema(example = "3")
    private Integer idProducto;

    @NotNull(message = "Elige el tipo de movimiento")
    @Schema(example = "ENTRADA")
    private TipoMovimiento tipoMovimiento;

    /** En ENTRADA y SALIDA es cuanto entra o sale; en AJUSTE es el stock contado. */
    @NotNull(message = "Ingresa la cantidad")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Max(value = 100000, message = "La cantidad es demasiado grande")
    @Schema(example = "10")
    private Integer cantidad;

    @NotBlank(message = "Indica el motivo del movimiento")
    @Size(min = 5, max = 200, message = "El motivo debe tener entre 5 y 200 caracteres")
    @Schema(example = "Compra a proveedor, guia 001-4587")
    private String motivo;
}
