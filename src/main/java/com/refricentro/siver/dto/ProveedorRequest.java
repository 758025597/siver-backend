package com.refricentro.siver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Lo que ENTRA por la API al crear o actualizar un proveedor.
 *
 * Reflejo exacto de la tabla proveedor:
 *   ck_proveedor_ruc       ->  '^(10|15|17|20)[0-9]{9}$'
 *   ck_proveedor_razon     ->  CHAR_LENGTH(TRIM(razon_social)) >= 3
 *   ck_proveedor_telefono  ->  '^[0-9]{9}$'
 *
 * OJO con el teléfono: el de proveedor admite CUALQUIER número de 9 dígitos,
 * a diferencia del de cliente y usuario, que exigen que empiece en 9. Es a
 * propósito: un proveedor es una empresa y puede tener teléfono fijo.
 */
@Data
public class ProveedorRequest {

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "(10|15|17|20)\\d{9}",
            message = "El RUC debe tener 11 dígitos y empezar en 10, 15, 17 o 20")
    private String ruc;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 3, max = 150, message = "La razón social debe tener entre 3 y 150 caracteres")
    private String razonSocial;

    @Size(max = 100, message = "El contacto no puede superar los 100 caracteres")
    private String contacto;

    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;
}
