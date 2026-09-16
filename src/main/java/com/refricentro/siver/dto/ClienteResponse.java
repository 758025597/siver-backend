package com.refricentro.siver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Integer idCliente;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String telefono;
    private String correo;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}