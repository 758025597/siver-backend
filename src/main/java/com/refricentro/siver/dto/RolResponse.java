
package com.refricentro.siver.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Lo que sale por la API cuando se consulta un rol. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}
