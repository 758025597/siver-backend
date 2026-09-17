package com.refricentro.siver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Lo que ENTRA por la API al crear o actualizar un cliente.
 *
 * Reflejo exacto de la tabla cliente:
 *   tipo_documento  ENUM('DNI','RUC','CE')
 *   ck_cliente_nombres   ->  CHAR_LENGTH(TRIM(nombres)) >= 3
 *   ck_cliente_telefono  ->  '^9[0-9]{8}$'   (celular: empieza en 9)
 *   ck_cliente_documento ->  la longitud depende del TIPO de documento
 */
@Data
public class ClienteRequest {

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "DNI|RUC|CE", message = "El tipo de documento debe ser DNI, RUC o CE")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 12, message = "El número de documento no puede superar los 12 caracteres")
    private String numeroDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 3, max = 150, message = "Los nombres deben tener entre 3 y 150 caracteres")
    private String nombres;

    @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y comenzar con 9")
    private String telefono;

    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;

    /**
     * Valida el documento CONTRA SU TIPO, que es lo que exige ck_cliente_documento.
     *
     * No se puede hacer con un @Pattern normal porque la regla depende de otro
     * campo. Para esos casos Bean Validation ofrece @AssertTrue: se escribe un
     * método que devuelve true si la combinación es válida.
     */
    @JsonIgnore
    @AssertTrue(message = "El número no corresponde al tipo de documento: "
            + "DNI son 8 dígitos, RUC son 11 empezando en 10/15/17/20, CE son de 9 a 12 dígitos")
    public boolean isDocumentoValido() {
        // Si falta alguno, ya lo reportan los @NotBlank de arriba.
        if (tipoDocumento == null || numeroDocumento == null) {
            return true;
        }
        return switch (tipoDocumento) {
            case "DNI" -> numeroDocumento.matches("\\d{8}");
            case "RUC" -> numeroDocumento.matches("(10|15|17|20)\\d{9}");
            case "CE" -> numeroDocumento.matches("\\d{9,12}");
            default -> true; // un tipo invalido ya lo reporta el @Pattern
        };
    }
}
