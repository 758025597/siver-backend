package com.refricentro.siver.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
    @EqualsAndHashCode.Include
    private Integer idVenta;

    @Column(name = "numero_comprobante", nullable = false, length = 20)
    private String numeroComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false,
            columnDefinition = "ENUM('BOLETA','FACTURA','NOTA_VENTA')")
    private TipoComprobante tipoComprobante = TipoComprobante.BOLETA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    /** Vendedor que emitio el comprobante. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

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

    /**
     * Las lineas de la venta.
     *
     * mappedBy = "venta" significa que la llave foranea la manda DetalleVenta.
     * cascade = ALL hace que al guardar la venta se guarden sus lineas.
     */
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DetalleVenta> detalles = new ArrayList<>();

    /** Agrega una linea manteniendo los dos lados de la relacion sincronizados. */
    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
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