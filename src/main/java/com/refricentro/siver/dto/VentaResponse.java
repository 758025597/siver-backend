package com.refricentro.siver.dto;

import com.refricentro.siver.model.Venta;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La venta como sale por la API, con sus lineas incluidas.
 *
 * subtotal, igv y total vienen calculados por los triggers de MySQL,
 * no por la aplicacion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse {

    private Integer id;
    private String numeroComprobante;
    private Venta.TipoComprobante tipoComprobante;
    private Integer idCliente;
    /** Nombre del cliente, gracias a la relacion @ManyToOne. */
    private String nombreCliente;
    private Integer idUsuario;
    private LocalDateTime fechaVenta;
    private Venta.MetodoPago metodoPago;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private Venta.EstadoVenta estado;
    private String observacion;
    private List<DetalleVentaResponse> detalles;
}