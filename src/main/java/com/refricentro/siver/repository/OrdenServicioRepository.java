package com.refricentro.siver.repository;

import com.refricentro.siver.model.OrdenServicio;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.refricentro.siver.repository.IGenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Acceso a datos de las ordenes de servicio tecnico. */
@Repository
public interface OrdenServicioRepository extends IGenericRepository<OrdenServicio, Integer>{

    Optional<OrdenServicio> findByNumeroOrden(String numeroOrden);

    boolean existsByNumeroOrden(String numeroOrden);

    List<OrdenServicio> findByCliente_IdCliente(Integer idCliente);

    /** Ordenes asignadas a un tecnico. La columna se llama id_usuario. */
    List<OrdenServicio> findByTecnico_IdUsuario(Integer idUsuario);

    List<OrdenServicio> findByEstado_IdEstado(Integer idEstado);

    /**
     * Consulta JPQL para filtrar ordenes por rango de fecha de ingreso.
     * Los JOIN FETCH aseguran que cliente, tecnico y estado se carguen en un unico SELECT.
     */
    @Query("SELECT o FROM OrdenServicio o " +
           "JOIN FETCH o.cliente " +
           "JOIN FETCH o.tecnico " +
           "JOIN FETCH o.estado " +
           "WHERE o.fechaIngreso BETWEEN :desde AND :hasta " +
           "ORDER BY o.fechaIngreso DESC")
    List<OrdenServicio> findByRangoFechasIngreso(@Param("desde") LocalDateTime desde,
                                                @Param("hasta") LocalDateTime hasta);

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

    /**
     * Busqueda paginada por numero de orden, cliente, tecnico, equipo, marca, modelo o estado.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @Query("""
        SELECT o FROM OrdenServicio o JOIN o.cliente c JOIN o.tecnico t JOIN o.estado e
         WHERE LOWER(o.numeroOrden) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(t.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(t.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(o.equipo) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(o.marca) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(o.modelo) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<OrdenServicio> buscar(@Param("texto") String texto, Pageable pageable);
}
