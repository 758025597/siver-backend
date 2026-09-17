package com.refricentro.siver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Lo que ENTRA por la API al crear o actualizar un usuario.
 *
 * Cada validacion es el reflejo de un CHECK de la tabla usuario, para
 * devolver un 400 con mensaje claro en vez de un error crudo de MySQL:
 *
 *   ck_usuario_dni       -> ^[0-9]{8}$
 *   ck_usuario_telefono  -> NULL o ^9[0-9]{8}$   (celular peruano)
 *   ck_usuario_correo    -> formato de correo
 *   ck_usuario_nombres   -> minimo 2 caracteres
 *   ck_usuario_apellidos -> minimo 2 caracteres
 *
 * La clave se pide en texto plano: el servicio la cifra antes de guardarla.
 * Por eso aqui el minimo es 6 y no 20: los 20 del CHECK los cumple el hash,
 * que siempre tiene 60 caracteres.
 */
public record UsuarioRequest(

        @NotNull(message = "El rol es obligatorio")
        Integer idRol,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
        String apellidos,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
        String dni,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 120, message = "El correo no puede pasar de 120 caracteres")
        String correo,

        @NotBlank(message = "La clave es obligatoria")
        @Size(min = 6, max = 72, message = "La clave debe tener entre 6 y 72 caracteres")
        String clave,

        @Pattern(regexp = "9\\d{8}", message = "El celular debe tener 9 digitos y empezar en 9")
        String telefono) {
}
