package com.refricentro.siver.repository;

import com.refricentro.siver.model.Rol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.refricentro.siver.repository.IGenericRepository;

/**
 * Acceso a datos de la tabla rol.
 *
 * JpaRepository ya trae findAll, findById, save, delete y demas.
 * Aqui solo se agregan las consultas propias de rol.
 */
@Repository

public interface RolRepository extends IGenericRepository<Rol, Integer> {


    /** Solo los roles vigentes. El listado normal no debe mostrar los desactivados. */
    List<Rol> findByActivoTrue();

    /** Para validar que el nombre no se repita: la columna es UNIQUE. */
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Busqueda paginada por nombre o descripcion.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @Query("""
        SELECT r FROM Rol r
         WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Rol> buscar(@Param("texto") String texto, Pageable pageable);
}
