package com.refricentro.siver.model;

import com.refricentro.siver.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "movimiento_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    @EqualsAndHashCode.Include
    private Integer idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false,
            foreignKey = @ForeignKey(name = "fk_movimiento_producto"))
    private Producto producto;

    /** Puede ser null: la columna admite NULL (ON DELETE SET NULL). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario",
            foreignKey = @ForeignKey(name = "fk_movimiento_usuario"))
    private Usuario usuario;

    /** Null cuando el movimiento no vino de una venta (un ajuste, por ejemplo). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta",
            foreignKey = @ForeignKey(name = "fk_movimiento_venta"))
    private Venta venta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false,
            columnDefinition = "ENUM('ENTRADA','SALIDA','AJUSTE')")
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "motivo", length = 200)
    private String motivo;

    @Generated(event = EventType.INSERT)
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;
}