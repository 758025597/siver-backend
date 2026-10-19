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

    @NotBlank(message = "{proveedor.ruc.notblank}")
    @Pattern(regexp = "(10|15|17|20)\\d{9}",
            message = "{proveedor.ruc.pattern}")
    private String ruc;

    @NotBlank(message = "{proveedor.razonSocial.notblank}")
    @Size(min = 3, max = 150, message = "{proveedor.razonSocial.size}")
    private String razonSocial;

    @Size(max = 100, message = "{proveedor.contacto.size}")
    private String contacto;

    @Pattern(regexp = "\\d{9}", message = "{proveedor.telefono.pattern}")
    private String telefono;

    @Email(message = "{proveedor.correo.email}")
    @Size(max = 120, message = "{proveedor.correo.size}")
    private String correo;

    @Size(max = 200, message = "{proveedor.direccion.size}")
    private String direccion;
}
