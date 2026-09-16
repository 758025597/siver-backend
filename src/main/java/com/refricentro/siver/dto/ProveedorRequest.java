package com.refricentro.siver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequest {

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos")
    private String ruc;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 150, message = "La razón social no puede superar los 150 caracteres")
    private String razonSocial;

    @Size(max = 100, message = "El contacto no puede superar los 100 caracteres")
    private String contacto;

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