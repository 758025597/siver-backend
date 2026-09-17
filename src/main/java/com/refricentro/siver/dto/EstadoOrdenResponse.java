package com.refricentro.siver.dto;

/** Lo que SALE por la API al consultar un estado. */
public record EstadoOrdenResponse(
        Integer id,
        String nombre,
        String descripcion,
        Byte ordenFlujo,
        Boolean esFinal) {
}
