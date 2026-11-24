package com.refricentro.siver.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Relacion N:M entre Notificacion y Usuario: "este usuario ya leyo este aviso".
 *
 *   notificacion 1 ---- N notificacion_lectura N ---- 1 usuario
 *
 * No se usa un @ManyToMany directo porque la tabla intermedia guarda un dato
 * propio (fecha_lectura). Cuando la tabla intermedia tiene columnas extra,
 * se mapea como entidad con:
 *
 *   @EmbeddedId  la clave compuesta (id_notificacion, id_usuario)
 *   @MapsId      cada @ManyToOne llena su parte de la clave
 */
@Entity
@Table(name = "notificacion_lectura")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificacionLectura {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private NotificacionLecturaId id = new NotificacionLecturaId();

    @MapsId("idNotificacion")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_notificacion",
            foreignKey = @ForeignKey(name = "fk_lectura_notificacion"))
    @ToString.Exclude
    private Notificacion notificacion;

    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario",
            foreignKey = @ForeignKey(name = "fk_lectura_usuario"))
    @ToString.Exclude
    private Usuario usuario;

    @Column(name = "fecha_lectura", nullable = false)
    private LocalDateTime fechaLectura;

    public NotificacionLectura(Notificacion notificacion, Usuario usuario) {
        this.id = new NotificacionLecturaId(notificacion.getIdNotificacion(), usuario.getIdUsuario());
        this.notificacion = notificacion;
        this.usuario = usuario;
        this.fechaLectura = LocalDateTime.now();
    }
}
