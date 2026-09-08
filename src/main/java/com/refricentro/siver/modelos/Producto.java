package com.refricentro.siver.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.refricentro.siver.modelos.enums.UnidadMedida;

/** Mapeo de la tabla producto. */
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @NotBlank
    @Size(max = 30)
    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 300)
    @Column(name = "descripcion", length = 300)
    private String descripcion;

    /** FK obligatoria hacia categoria. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    /** FK opcional: id_proveedor admite NULL en la tabla. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    /** ENUM de MySQL guardado como texto, no como numero. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false,
            columnDefinition = "ENUM('UNIDAD','KILOGRAMO','LITRO','METRO','CAJA','GALON')")
    private UnidadMedida unidadMedida = UnidadMedida.UNIDAD;

    /** DECIMAL(10,2) como BigDecimal: nunca double, para no perder centavos. */
    @NotNull
    @PositiveOrZero
    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    /** Sin DEFAULT en la tabla: siempre lo define quien registra el producto. */
    @NotNull
    @Positive
    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @NotNull
    @PositiveOrZero
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @NotNull
    @PositiveOrZero
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 5;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /** La pone MySQL con DEFAULT CURRENT_TIMESTAMP; Hibernate la lee de vuelta. */
    @Generated(event = EventType.INSERT)
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
