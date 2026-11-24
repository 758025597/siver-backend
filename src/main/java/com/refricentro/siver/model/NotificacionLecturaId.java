package com.refricentro.siver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CLAVE PRIMARIA COMPUESTA de notificacion_lectura: (id_notificacion, id_usuario).
 *
 * Una clave compuesta en JPA se arma con una clase @Embeddable que:
 *   - implementa Serializable
 *   - tiene equals() y hashCode() (los genera @Data de Lombok)
 *   - tiene un constructor vacio
 *
 * La entidad la usa con @EmbeddedId. Asi la misma pareja (notificacion,
 * usuario) no puede repetirse: un usuario lee una notificacion una sola vez.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionLecturaId implements Serializable {

    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(name = "id_usuario")
    private Integer idUsuario;
}
