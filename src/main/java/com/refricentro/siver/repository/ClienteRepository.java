package com.refricentro.siver.repository;

import com.refricentro.siver.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends IGenericRepository<Cliente, Integer> {

    List<Cliente> findByActivoTrue();

    Optional<Cliente> findByIdClienteAndActivoTrue(Integer idCliente);

    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);

    /**
     * Busqueda paginada por nombre, documento, correo o telefono.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @Query("""
        SELECT c FROM Cliente c
         WHERE LOWER(c.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.numeroDocumento) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.correo) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.telefono) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Cliente> buscar(@Param("texto") String texto, Pageable pageable);
}
