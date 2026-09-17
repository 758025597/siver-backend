package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.EstadoOrden;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos del catalogo de estados de una orden de servicio. */
@Repository
public interface EstadoOrdenRepository extends JpaRepository<EstadoOrden, Integer> {

    /** Los estados en el orden del flujo de atencion: Recibido, En revision, ... */
    List<EstadoOrden> findAllByOrderByOrdenFlujoAsc();

    Optional<EstadoOrden> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByOrdenFlujo(Byte ordenFlujo);
}
