package com.refricentro.siver.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

/** Mapeo de la tabla usuario. */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements Activable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    /** FK hacia rol. LAZY: el rol se consulta solo cuando se usa. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @NotBlank
    @Size(max = 100)
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /** CHAR(8) en MySQL: se declara con columnDefinition para que validate lo acepte. */
    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 digitos")
    @Column(name = "dni", nullable = false, unique = true, columnDefinition = "CHAR(8)")
    private String dni;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(name = "correo", nullable = false, unique = true, length = 120)
    private String correo;

    @NotBlank
    @Size(max = 255)
    @Column(name = "clave", nullable = false, length = 255)
    private String clave;

    @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos")
    @Column(name = "telefono", columnDefinition = "CHAR(9)")
    private String telefono;

    /** NOT NULL DEFAULT 1: se inicializa en Java para no insertar NULL. */
    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    /** La pone MySQL con DEFAULT CURRENT_TIMESTAMP; Hibernate la lee de vuelta. */
    @Generated(event = EventType.INSERT)
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
