package com.refricentro.siver.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo que SALE por la API cuando se consulta un usuario.
 *
 * IMPORTANTE: aqui NO existe el campo clave, ni cifrada.
 * Si se devolviera la entidad Usuario tal cual, la API estaria publicando
 * las contrasenas de todos. Esa es la razon principal de que existan los DTO.
 *
 * Se incluye nombreRol para que el frontend no tenga que hacer una segunda
 * consulta solo para mostrar a que rol pertenece el usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Integer id;
    private Integer idRol;
    private String nombreRol;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;
    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime fechaRegistro;
}