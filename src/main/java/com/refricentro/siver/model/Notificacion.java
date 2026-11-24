package com.refricentro.siver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Un aviso del sistema: stock bajo, orden vencida, venta anulada...
 *
 * rolesDestino dice a quien le aparece: "ADMINISTRADOR,VENDEDOR".
 * Si es null, la ven todos.
 *
 * referencia identifica el objeto del aviso ("producto:12", "orden:7") y
 * sirve para no repetir el mismo aviso varias veces en el mismo dia.
 *
 * Que usuario ya la leyo se guarda aparte, en NotificacionLectura (N:M).
 */
@Entity
@Table(name = "notificacion")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    @EqualsAndHashCode.Include
    private Integer idNotificacion;

    /** STOCK_BAJO, ORDEN_VENCIDA, ORDEN_FINALIZADA, VENTA_ANULADA. */
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "titulo", nullable = false, length = 120)
    private String titulo;

    @Column(name = "mensaje", nullable = false, length = 300)
    private String mensaje;

    /** Ruta de Angular a la que lleva el aviso, por ejemplo /pages/productos. */
    @Column(name = "enlace", length = 200)
    private String enlace;

    @Column(name = "roles_destino", length = 120)
    private String rolesDestino;

    @Column(name = "referencia", length = 50)
    private String referencia;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
