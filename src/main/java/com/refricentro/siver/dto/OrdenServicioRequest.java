
package com.refricentro.siver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo que entra por la API al registrar o actualizar una orden de servicio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenServicioRequest {

        @Schema(description = "Número único de la orden de servicio", example = "OS-00001")
        @NotBlank(message = "El número de orden es obligatorio")
        @Size(max = 20, message = "El número de orden no puede superar los 20 caracteres")
        private String numeroOrden;

        @Schema(description = "Identificador del cliente que solicita el servicio", example = "1")
        @NotNull(message = "El cliente es obligatorio")
        private Integer idCliente;

        @Schema(description = "Identificador del técnico responsable de la atención", example = "2")
        @NotNull(message = "El técnico es obligatorio")
        private Integer idUsuario;

        @Schema(description = "Identificador del estado inicial de la orden. Solo se utiliza al crearla", example = "1")
        private Integer idEstado;

        @Schema(description = "Nombre o descripción del equipo recibido", example = "Refrigeradora")
        @NotBlank(message = "El equipo es obligatorio")
        @Size(min = 3, max = 100, message = "El equipo debe tener entre 3 y 100 caracteres")
        private String equipo;

        @Schema(description = "Marca del equipo", example = "Samsung")
        @Size(max = 60, message = "La marca no puede superar los 60 caracteres")
        private String marca;

        @Schema(description = "Modelo del equipo", example = "RT38K5930SL")
        @Size(max = 60, message = "El modelo no puede superar los 60 caracteres")
        private String modelo;

        @Schema(description = "Número de serie del equipo", example = "SN123456789")
        @Size(max = 60, message = "El número de serie no puede superar los 60 caracteres")
        private String numeroSerie;

        @Schema(description = "Descripción detallada de la falla reportada", example = "La refrigeradora no enfría correctamente")
        @NotBlank(message = "La descripción de la falla es obligatoria")
        @Size(min = 10, message = "Describe la falla con al menos 10 caracteres")
        private String descripcionFalla;

        @Schema(description = "Diagnóstico técnico del equipo", example = "Falla en el compresor")
        private String diagnostico;

        @Schema(description = "Descripción del trabajo realizado", example = "Se reemplazó el compresor y se verificó el funcionamiento")
        private String trabajoRealizado;

        @Schema(description = "Fecha estimada de entrega del equipo", example = "2026-11-15")
        private LocalDate fechaEntregaEstimada;

        @Schema(description = "Costo estimado del servicio", example = "250.00")
        @NotNull(message = "El costo estimado es obligatorio")
        @PositiveOrZero(message = "El costo estimado no puede ser negativo")
        private BigDecimal costoEstimado;

        @Schema(description = "Costo final del servicio", example = "280.00")
        @PositiveOrZero(message = "El costo final no puede ser negativo")
        private BigDecimal costoFinal;
}
