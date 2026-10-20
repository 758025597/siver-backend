package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.refricentro.siver.model.Venta;
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
 * Una venta COMPLETA: la cabecera y sus lineas.
 *
 * No se piden subtotal, igv ni total: los calcula
 * trg_detalle_venta_after_insert con IGV del 18%. Si la API los aceptara,
 * alguien podria mandar un total que no cuadre con las lineas.
 *
 * Tampoco se pide la fecha (la pone MySQL) ni el estado (nace EMITIDA).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {

    @NotBlank(message = "{venta.numeroComprobante.notblank}")
    @Size(max = 20, message = "{venta.numeroComprobante.size}")
    private String numeroComprobante;

    @NotNull(message = "{venta.tipoComprobante.notnull}")
    private Venta.TipoComprobante tipoComprobante;

    @NotNull(message = "{venta.idCliente.notnull}")
    private Integer idCliente;

    /** Vendedor que emite el comprobante. */
    @NotNull(message = "{venta.idUsuario.notnull}")
    private Integer idUsuario;

    @NotNull(message = "{venta.metodoPago.notnull}")
    private Venta.MetodoPago metodoPago;

    @Size(max = 300, message = "{venta.observacion.size}")
    private String observacion;

    /**
     * Las lineas. El @Valid hace que se validen una por una:
     * sin el, Bean Validation solo comprueba que la lista no este vacia.
     */
    @NotEmpty(message = "{venta.detalles.notempty}")
    @Valid
    private List<DetalleVentaRequest> detalles;

    /**
     * Valida uq_detalle_venta_producto: el mismo producto no puede ir dos
     * veces en la misma venta. Si se necesitan 5 unidades, va una sola linea
     * con cantidad 5.
     */
    @JsonIgnore
    @AssertTrue(message = "{venta.sinProductosRepetidos.asserttrue}")
    public boolean isSinProductosRepetidos() {
        if (detalles == null) {
            return true;
        }
        Set<Integer> vistos = new HashSet<>();
        return detalles.stream()
                .map(DetalleVentaRequest::getIdProducto) // Ojo aquí: ahora usa getIdProducto() porque DetalleVentaRequest también será clase
                .filter(java.util.Objects::nonNull)
                .allMatch(vistos::add);
    }
}