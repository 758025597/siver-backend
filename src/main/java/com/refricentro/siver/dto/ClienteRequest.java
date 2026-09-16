package com.refricentro.siver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 12, message = "El número de documento no puede superar los 12 caracteres")
    private String numeroDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 150, message = "Los nombres no pueden superar los 150 caracteres")
    private String nombres;

    @Pattern(
            regexp = "^$|9\\d{8}",
            message = "El teléfono debe tener 9 dígitos y comenzar con 9"
    )
    private String telefono;

    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;
}