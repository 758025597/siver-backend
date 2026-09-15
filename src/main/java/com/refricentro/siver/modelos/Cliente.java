package com.refricentro.siver.modelos;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "Clientes")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idCliente;

    @Column(nullable = false, length = 20)
    private String tipoDocumento;

    @Column(nullable = false, length = 12, unique = true)
    private String numeroDocumento;

    @Column(nullable = false, length = 150)
    private String nombres;

    @Column(length = 9)
    private String telefono;

    @Column(length = 120)
    private String correo;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(updatable = false)
    private LocalDateTime fechaRegistro;
}
