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
 * Una fila de la bitacora de auditoria: QUIEN hizo QUE y CUANDO.
 *
 * La escribe AuditoriaInterceptor despues de cada POST, PUT, PATCH o DELETE
 * de la API, y AuthController en cada inicio de sesion. Nunca se edita ni se
 * borra: por eso no tiene setters especiales ni endpoints de escritura.
 *
 * id_usuario se guarda como numero suelto (no como @ManyToOne) para que el
 * registro siga intacto aunque el usuario cambie de rol o de nombre: la
 * auditoria guarda el correo y el rol que tenia EN ESE MOMENTO.
 */
@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    @EqualsAndHashCode.Include
    private Long idAuditoria;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    /** El correo con el que se identifico (o el que se intento en un login fallido). */
    @Column(name = "usuario", nullable = false, length = 120)
    private String usuario;

    @Column(name = "rol", length = 50)
    private String rol;

    /** CREAR, ACTUALIZAR, ELIMINAR, ANULAR, CAMBIAR_ESTADO, LOGIN, LOGIN_FALLIDO... */
    @Column(name = "accion", nullable = false, length = 20)
    private String accion;

    /** clientes, ventas, productos... (el recurso de la URL). */
    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    @Column(name = "id_registro", length = 20)
    private String idRegistro;

    @Column(name = "metodo", nullable = false, length = 10)
    private String metodo;

    @Column(name = "ruta", nullable = false, length = 200)
    private String ruta;

    @Column(name = "estado_http", nullable = false)
    private Integer estadoHttp;

    @Column(name = "ip", length = 45)
    private String ip;
}
