package com.refricentro.siver.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato unico de error que devuelve la API.
 * Asi el frontend Angular siempre recibe la misma estructura.
 *
 * `campos` solo viene cuando fallan validaciones, con el detalle por campo.
 */
public record ErrorRespuesta(
        LocalDateTime momento,
        int estado,
        String error,
        String mensaje,
        Map<String, String> campos) {

    public static ErrorRespuesta de(int estado, String error, String mensaje) {
        return new ErrorRespuesta(LocalDateTime.now(), estado, error, mensaje, null);
    }
}
