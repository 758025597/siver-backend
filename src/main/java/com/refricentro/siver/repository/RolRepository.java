package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.Rol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de la tabla rol.
 *
 * JpaRepository ya trae findAll, findById, save, delete y demas.
 * Aqui solo se agregan las consultas propias de rol.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /** Solo los roles vigentes. El listado normal no debe mostrar los desactivados. */
    List<Rol> findByActivoTrue();

    /** Para validar que el nombre no se repita: la columna es UNIQUE. */
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
