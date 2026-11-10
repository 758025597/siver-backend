package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(
            description = "Número de Registro Único de Contribuyentes del proveedor",
            example = "20123456789"
    )
    private String ruc;

    @Schema(
            description = "Razón social o nombre legal de la empresa proveedora",
            example = "Distribuidora Andina S.A.C."
    )
    @NotBlank(message = "{proveedor.razonSocial.notblank}")
    @Size(min = 3, max = 150, message = "{proveedor.razonSocial.size}")
    private String razonSocial;

    @Schema(
            description = "Nombre de la persona de contacto del proveedor",
            example = "Carlos Mendoza"
    )
    @Size(max = 100, message = "{proveedor.contacto.size}")
    private String contacto;

    @Schema(
            description = "Número telefónico de contacto del proveedor, de 9 dígitos",
            example = "076362514"
    )
    @Pattern(regexp = "\\d{9}", message = "{proveedor.telefono.pattern}")
    private String telefono;

    @Schema(
            description = "Correo electrónico de contacto del proveedor",
            example = "ventas@distribuidoraandina.com"
    )
    @Email(message = "{proveedor.correo.email}")
    @Size(max = 120, message = "{proveedor.correo.size}")
    private String correo;

    @Schema(
            description = "Dirección fiscal o comercial del proveedor",
            example = "Av. Atahualpa 456, Cajamarca"
    )
    @Size(max = 200, message = "{proveedor.direccion.size}")
    private String direccion;
}
