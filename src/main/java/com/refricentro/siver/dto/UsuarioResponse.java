package com.refricentro.siver.dto;

import java.time.LocalDateTime;

/**
 * Lo que SALE por la API cuando se consulta un usuario.
 *
 * IMPORTANTE: aqui NO existe el campo `clave`, ni cifrada.
 * Si se devolviera la entidad Usuario tal cual, la API estaria publicando
 * las contrasenas de todos. Esa es la razon principal de que existan los DTO.
 *
 * Se incluye nombreRol para que el frontend no tenga que hacer una segunda
 * consulta solo para mostrar a que rol pertenece el usuario.
 */
public record UsuarioResponse(
        Integer id,
        Integer idRol,
        String nombreRol,
        String nombres,
        String apellidos,
        String dni,
        String correo,
        String telefono,
        Boolean activo,
        LocalDateTime ultimoAcceso,
        LocalDateTime fechaRegistro) {
}
