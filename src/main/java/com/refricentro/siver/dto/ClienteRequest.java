
package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Lo que ENTRA por la API al crear o actualizar un cliente.
 */
@Data
public class ClienteRequest {

    @Schema(
            description = "Tipo de documento de identidad del cliente",
            example = "DNI",
            allowableValues = {"DNI", "RUC", "CE"}
    )
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "DNI|RUC|CE", message = "El tipo de documento debe ser DNI, RUC o CE")
    private String tipoDocumento;

    @Schema(
            description = "Número de documento de identidad del cliente",
            example = "12345678"
    )
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 12, message = "El número de documento no puede superar los 12 caracteres")
    private String numeroDocumento;

    @Schema(
            description = "Nombres completos del cliente",
            example = "María Fernanda Torres"
    )
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 3, max = 150, message = "Los nombres deben tener entre 3 y 150 caracteres")
    private String nombres;

    @Schema(
            description = "Número de celular peruano del cliente",
            example = "987654321"
    )
    @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y comenzar con 9")
    private String telefono;

    @Schema(
            description = "Correo electrónico del cliente",
            example = "maria.torres@gmail.com"
    )
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @Schema(
            description = "Dirección de domicilio del cliente",
            example = "Jr. Amazonas 123, Cajamarca"
    )
    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;

    /**
     * Valida el documento contra su tipo.
     */
    @JsonIgnore
    @AssertTrue(message = "El número no corresponde al tipo de documento: "
            + "DNI son 8 dígitos, RUC son 11 empezando en 10/15/17/20, CE son de 9 a 12 dígitos")
    public boolean isDocumentoValido() {
        if (tipoDocumento == null || numeroDocumento == null) {
            return true;
        }
        return switch (tipoDocumento) {
            case "DNI" -> numeroDocumento.matches("\\d{8}");
            case "RUC" -> numeroDocumento.matches("(10|15|17|20)\\d{9}");
            case "CE" -> numeroDocumento.matches("\\d{9,12}");
            default -> true;
        };
    }
}
