package com.refricentro.siver.modelos;

import com.refricentro.siver.modelos.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "movimiento_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_venta")
    private Integer idVenta;

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