package com.refricentro.siver.modelos;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
@Data
@Entity
@Table(name = "cliente")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "tipo_documento", nullable = false,
            columnDefinition = "ENUM('DNI','RUC','CE')")
    private String tipoDocumento = "DNI";

    @Column(nullable = false, length = 12, unique = true)
    private String numeroDocumento;

    @Column(nullable = false, length = 150)
    private String nombres;

    @Column(name = "telefono", columnDefinition = "CHAR(9)")
    private String telefono;

    @Column(length = 120)
    private String correo;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Generated(event = EventType.INSERT)
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
