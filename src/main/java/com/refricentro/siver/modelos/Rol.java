package com.refricentro.siver.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Data
@Entity
@Table(name = "rol")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(length = 100)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Generated(event = EventType.INSERT)
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

}
