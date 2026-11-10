
package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Lo que ENTRA por la API al crear o actualizar un proveedor.
 */
@Data
public class ProveedorRequest {

    @Schema(
            description = "Número de Registro Único de Contribuyentes del proveedor",
            example = "20123456789"
    )
    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "(10|15|17|20)\\d{9}",
            message = "El RUC debe tener 11 dígitos y empezar en 10, 15, 17 o 20")
    private String ruc;

    @Schema(
            description = "Razón social o nombre legal de la empresa proveedora",
            example = "Distribuidora Andina S.A.C."
    )
    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 3, max = 150, message = "La razón social debe tener entre 3 y 150 caracteres")
    private String razonSocial;

    @Schema(
            description = "Nombre de la persona de contacto del proveedor",
            example = "Carlos Mendoza"
    )
    @Size(max = 100, message = "El contacto no puede superar los 100 caracteres")
    private String contacto;

    @Schema(
            description = "Número telefónico de contacto del proveedor, de 9 dígitos",
            example = "076362514"
    )
    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @Schema(
            description = "Correo electrónico de contacto del proveedor",
            example = "ventas@distribuidoraandina.com"
    )
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @Schema(
            description = "Dirección fiscal o comercial del proveedor",
            example = "Av. Atahualpa 456, Cajamarca"
    )
    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;
}