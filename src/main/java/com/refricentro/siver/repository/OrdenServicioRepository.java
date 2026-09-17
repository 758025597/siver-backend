package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.OrdenServicio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de las ordenes de servicio tecnico. */
@Repository
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Integer> {

    Optional<OrdenServicio> findByNumeroOrden(String numeroOrden);

    boolean existsByNumeroOrden(String numeroOrden);

    List<OrdenServicio> findByIdCliente(Integer idCliente);

    /** Ordenes asignadas a un tecnico. La columna se llama id_usuario. */
    List<OrdenServicio> findByIdUsuario(Integer idUsuario);

    List<OrdenServicio> findByIdEstado(Integer idEstado);
}
