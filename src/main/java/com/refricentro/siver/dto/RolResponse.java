package com.refricentro.siver.dto;

import java.time.LocalDateTime;

/** Lo que SALE por la API cuando se consulta un rol. */
public record RolResponse(
        Integer id,
        String nombre,
        String descripcion,
        Boolean activo,
        LocalDateTime fechaRegistro) {
}
