package com.refricentro.siver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Lo que SALE por la API al consultar un estado. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoOrdenResponse {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Byte ordenFlujo;
    private Boolean esFinal;
}