package com.refricentro.siver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {

    private Integer idProveedor;
    private String ruc;
    private String razonSocial;
    private String contacto;
    private String telefono;
    private String correo;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}