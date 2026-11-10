
package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.refricentro.siver.model.Venta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Una venta completa: la cabecera y sus lineas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {

    @Schema(description = "Número del comprobante de venta", example = "B001-0000125")
    @NotBlank(message = "El número de comprobante es obligatorio")
    @Size(max = 20, message = "El número de comprobante no puede superar los 20 caracteres")
    private String numeroComprobante;

    @Schema(description = "Tipo de comprobante de venta", example = "BOLETA")
    @NotNull(message = "El tipo de comprobante es obligatorio")
    private Venta.TipoComprobante tipoComprobante;

    @Schema(description = "Identificador del cliente que realiza la compra", example = "1")
    @NotNull(message = "El cliente es obligatorio")
    private Integer idCliente;

    @Schema(description = "Identificador del vendedor que emite el comprobante", example = "2")
    @NotNull(message = "El vendedor es obligatorio")
    private Integer idUsuario;

    @Schema(description = "Método de pago utilizado", example = "EFECTIVO")
    @NotNull(message = "El método de pago es obligatorio")
    private Venta.MetodoPago metodoPago;

    @Schema(description = "Observaciones adicionales de la venta", example = "Entrega en tienda")
    @Size(max = 300, message = "La observación no puede superar los 300 caracteres")
    private String observacion;

    @Schema(
            description = "Lista de productos incluidos en la venta",
            example = "[{\"idProducto\":1,\"cantidad\":2}]"
    )
    @NotEmpty(message = "La venta debe tener al menos un producto")
    @Valid
    private List<DetalleVentaRequest> detalles;

    /**
     * Comprueba que no existan productos repetidos en la misma venta.
     */
    @JsonIgnore
    @AssertTrue(message = "Hay un producto repetido: agrupa las unidades en una sola línea")
    public boolean isSinProductosRepetidos() {
        if (detalles == null) {
            return true;
        }

        Set<Integer> vistos = new HashSet<>();
        return detalles.stream()
                .map(DetalleVentaRequest::getIdProducto)
                .filter(java.util.Objects::nonNull)
                .allMatch(vistos::add);
    }
}
