package com.refricentro.siver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo;
    private long expiraEnMs;
    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String rol;
}