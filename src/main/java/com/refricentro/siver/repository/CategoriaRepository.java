
package com.refricentro.siver.repository;

import com.refricentro.siver.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends IGenericRepository<Categoria, Integer> {

    List<Categoria> findByActivoTrue();

    Optional<Categoria> findByIdCatAndActivoTrue(Integer idCat);

    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Busqueda paginada por nombre o descripcion.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @Query("""
        SELECT c FROM Categoria c
         WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Categoria> buscar(@Param("texto") String texto, Pageable pageable);
}
