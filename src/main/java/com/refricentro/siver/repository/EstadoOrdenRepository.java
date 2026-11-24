package com.refricentro.siver.repository;

import com.refricentro.siver.model.EstadoOrden;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.refricentro.siver.repository.IGenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Acceso a datos del catalogo de estados de una orden de servicio. */
@Repository
public interface EstadoOrdenRepository extends IGenericRepository<EstadoOrden, Integer> {

    /** Los estados en el orden del flujo de atencion: Recibido, En revision, ... */
    List<EstadoOrden> findAllByOrderByOrdenFlujoAsc();

    Optional<EstadoOrden> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByOrdenFlujo(Byte ordenFlujo);

    /**
     * Busqueda paginada por nombre o descripcion.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @Query("""
        SELECT e FROM EstadoOrden e
         WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<EstadoOrden> buscar(@Param("texto") String texto, Pageable pageable);
}
