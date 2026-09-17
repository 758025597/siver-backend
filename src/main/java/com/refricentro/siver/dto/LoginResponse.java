package com.refricentro.siver.dto;

/**
 * Lo que devuelve el login.
 *
 * El `token` es el que hay que mandar en las siguientes peticiones:
 *     Authorization: Bearer <token>
 *
 * Se devuelven tambien los datos basicos del usuario para que el frontend
 * pueda mostrar su nombre y decidir que menus enseñar segun el rol, sin
 * tener que hacer otra consulta.
 */
public record LoginResponse(
        String token,
        String tipo,
        long expiraEnMs,
        Integer idUsuario,
        String nombres,
        String apellidos,
        String correo,
        String rol) {
}
