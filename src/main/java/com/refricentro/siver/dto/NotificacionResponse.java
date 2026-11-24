package com.refricentro.siver.dto;

import java.time.LocalDateTime;

/** Un aviso tal como lo ve el usuario, con su estado de lectura. */
public record NotificacionResponse(
        Integer idNotificacion,
        String tipo,
        String titulo,
        String mensaje,
        String enlace,
        LocalDateTime fecha,
        boolean leida) {
}
