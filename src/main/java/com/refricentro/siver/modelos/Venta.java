package com.refricentro.siver.modelos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(
        name = "venta",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_venta_comprobante",
                        columnNames = {"tipo_comprobante", "numero_comprobante"}
                )
        }
)
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "numero_comprobante", nullable = false, length = 20)
    private String numeroComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false,
            columnDefinition = "ENUM('BOLETA','FACTURA','NOTA_VENTA')")
    private TipoComprobante tipoComprobante = TipoComprobante.BOLETA;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Generated(event = EventType.INSERT)
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false,
            columnDefinition = "ENUM('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA')")
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "igv", nullable = false, precision = 10, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false,
            columnDefinition = "ENUM('EMITIDA','ANULADA')")
    private EstadoVenta estado = EstadoVenta.EMITIDA;

    @Column(name = "observacion", length = 300)
    private String observacion;


    public Venta() {
    }


    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }


    public enum TipoComprobante {
        BOLETA,
        FACTURA,
        NOTA_VENTA
    }


    public enum MetodoPago {
        EFECTIVO,
        TARJETA,
        YAPE,
        PLIN,
        TRANSFERENCIA
    }


    public enum EstadoVenta {
        EMITIDA,
        ANULADA
    }
}
