package com.refricentro.siver.dto;

import com.refricentro.siver.modelos.enums.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Integer idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;

    private Integer idCategoria;
    private Integer idProveedor;

    private UnidadMedida unidadMedida;

    private BigDecimal precioCompra;
    private BigDecimal precioVenta;

    private Integer stock;
    private Integer stockMinimo;

    private Boolean activo;
    private LocalDateTime fechaRegistro;
}