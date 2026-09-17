package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.OrdenServicio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de las ordenes de servicio tecnico. */
@Repository
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Integer> {

    Optional<OrdenServicio> findByNumeroOrden(String numeroOrden);

    boolean existsByNumeroOrden(String numeroOrden);

    List<OrdenServicio> findByCliente_IdCliente(Integer idCliente);

    /** Ordenes asignadas a un tecnico. La columna se llama id_usuario. */
    List<OrdenServicio> findByTecnico_IdUsuario(Integer idUsuario);

    List<OrdenServicio> findByEstado_IdEstado(Integer idEstado);

    /**
     * Trae OrdenServicio junto con sus relaciones en un solo SELECT con JOIN.
     *
     * Sin esto, al armar la respuesta fuera de la transaccion Hibernate
     * lanzaria LazyInitializationException, porque las relaciones son LAZY.
     */
    @Override
    @EntityGraph(attributePaths = {"cliente", "tecnico", "estado"})
    List<OrdenServicio> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "tecnico", "estado"})
    Optional<OrdenServicio> findById(Integer id);
}
