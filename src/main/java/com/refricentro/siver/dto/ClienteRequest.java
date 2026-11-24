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
 *
 * Reflejo exacto de la tabla cliente:
 *   tipo_documento  ENUM('DNI','RUC','CE')
 *   ck_cliente_nombres   ->  CHAR_LENGTH(TRIM(nombres)) >= 3
 *   ck_cliente_telefono  ->  '^9[0-9]{8}$'   (celular: empieza en 9)
 *   ck_cliente_documento ->  la longitud depende del TIPO de documento
 */
@Data
public class ClienteRequest {

    @Schema(
            description = "Tipo de documento de identidad del cliente",
            example = "DNI",
            allowableValues = {"DNI", "RUC", "CE"}
    )
    @NotBlank(message = "{cliente.tipoDocumento.notblank}")
    @Pattern(regexp = "DNI|RUC|CE", message = "{cliente.tipoDocumento.pattern}")
    private String tipoDocumento;

    @Schema(
            description = "Número de documento de identidad del cliente",
            example = "12345678"
    )
    @NotBlank(message = "{cliente.numeroDocumento.notblank}")
    @Size(max = 12, message = "{cliente.numeroDocumento.size}")
    private String numeroDocumento;

    @Schema(
            description = "Nombres completos del cliente",
            example = "María Fernanda Torres"
    )
    @NotBlank(message = "{cliente.nombres.notblank}")
    @Size(min = 3, max = 150, message = "{cliente.nombres.size}")
    private String nombres;

    @Schema(
            description = "Número de celular peruano del cliente",
            example = "987654321"
    )
    @Pattern(regexp = "9\\d{8}", message = "{cliente.telefono.pattern}")
    private String telefono;

    @Schema(
            description = "Correo electrónico del cliente",
            example = "maria.torres@gmail.com"
    )
    @Email(message = "{cliente.correo.email}")
    @Size(max = 120, message = "{cliente.correo.size}")
    private String correo;

    @Schema(
            description = "Dirección de domicilio del cliente",
            example = "Jr. Amazonas 123, Cajamarca"
    )
    @Size(max = 200, message = "{cliente.direccion.size}")
    private String direccion;

    /**
     * Valida el documento CONTRA SU TIPO, que es lo que exige ck_cliente_documento.
     *
     * No se puede hacer con un @Pattern normal porque la regla depende de otro
     * campo. Para esos casos Bean Validation ofrece @AssertTrue: se escribe un
     * método que devuelve true si la combinación es válida.
     */
    @JsonIgnore
    @AssertTrue(message = "{cliente.documentoValido.asserttrue}"
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
