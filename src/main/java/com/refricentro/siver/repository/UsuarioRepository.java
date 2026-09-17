package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de la tabla usuario.
 *
 * Sobre el @EntityGraph: la relacion usuario -> rol es LAZY, o sea que el
 * rol no se carga hasta que alguien lo pide. Si el controlador intentara
 * leer usuario.getRol().getNombre() ya fuera de la transaccion, reventaria
 * con LazyInitializationException.
 *
 * @EntityGraph le dice a Hibernate que en ESTAS consultas traiga el rol
 * junto con el usuario, en un solo SELECT con JOIN. Asi el controlador
 * puede armar la respuesta sin problemas y sin consultas de mas.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Override
    @EntityGraph(attributePaths = "rol")
    List<Usuario> findAll();

    @Override
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findById(Integer id);

    @EntityGraph(attributePaths = "rol")
    List<Usuario> findByActivoTrue();

    /** Para el login y para validar que el correo no se repita. */
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByDni(String dni);
}
