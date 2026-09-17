package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.refricentro.siver.modelos.Venta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Una venta COMPLETA: la cabecera y sus lineas.
 *
 * No se piden subtotal, igv ni total: los calcula
 * trg_detalle_venta_after_insert con IGV del 18%. Si la API los aceptara,
 * alguien podria mandar un total que no cuadre con las lineas.
 *
 * Tampoco se pide la fecha (la pone MySQL) ni el estado (nace EMITIDA).
 */
public record VentaRequest(

        @NotBlank(message = "El número de comprobante es obligatorio")
        @Size(max = 20, message = "El número de comprobante no puede superar los 20 caracteres")
        String numeroComprobante,

        @NotNull(message = "El tipo de comprobante es obligatorio")
        Venta.TipoComprobante tipoComprobante,

        @NotNull(message = "El cliente es obligatorio")
        Integer idCliente,

        /** Vendedor que emite el comprobante. */
        @NotNull(message = "El vendedor es obligatorio")
        Integer idUsuario,

        @NotNull(message = "El método de pago es obligatorio")
        Venta.MetodoPago metodoPago,

        @Size(max = 300, message = "La observación no puede superar los 300 caracteres")
        String observacion,

        /**
         * Las lineas. El @Valid hace que se validen una por una:
         * sin el, Bean Validation solo comprueba que la lista no este vacia.
         */
        @NotEmpty(message = "La venta debe tener al menos un producto")
        @Valid
        List<DetalleVentaRequest> detalles) {

    /**
     * Valida uq_detalle_venta_producto: el mismo producto no puede ir dos
     * veces en la misma venta. Si se necesitan 5 unidades, va una sola linea
     * con cantidad 5.
     */
    @JsonIgnore
    @AssertTrue(message = "Hay un producto repetido: agrupa las unidades en una sola línea")
    public boolean isSinProductosRepetidos() {
        if (detalles == null) {
            return true;
        }
        Set<Integer> vistos = new HashSet<>();
        return detalles.stream()
                .map(DetalleVentaRequest::idProducto)
                .filter(java.util.Objects::nonNull)
                .allMatch(vistos::add);
    }
}
