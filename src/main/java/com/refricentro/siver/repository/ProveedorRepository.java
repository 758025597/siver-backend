
package com.refricentro.siver.repository;

import com.refricentro.siver.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends IGenericRepository<Proveedor, Integer> {

    List<Proveedor> findByActivoTrue();

    Optional<Proveedor> findByIdProveedorAndActivoTrue(Integer idProveedor);

    @Query("SELECT p FROM Proveedor p WHERE p.ruc = :ruc")
    Optional<Proveedor> findByRuc(@Param("ruc") String ruc);

    boolean existsByRuc(String ruc);

    /**
     * Busqueda paginada por RUC, razon social, contacto, correo o telefono.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @Query("""
        SELECT p FROM Proveedor p
         WHERE LOWER(p.ruc) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(p.contacto) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(p.correo) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(p.telefono) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Proveedor> buscar(@Param("texto") String texto, Pageable pageable);
}
