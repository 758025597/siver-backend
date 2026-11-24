package com.refricentro.siver.repository;

import com.refricentro.siver.model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
public interface UsuarioRepository extends IGenericRepository<Usuario, Integer> {

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

    /**
     * Consulta de MODIFICACION: actualiza solo la columna ultimo_acceso.
     *
     * @Modifying le avisa a Spring que esto no es un SELECT, y por eso tiene
     * que correr dentro de una transaccion (@Transactional del servicio).
     * clearAutomatically limpia la cache de Hibernate despues del UPDATE, para
     * que nadie siga leyendo el usuario con la fecha vieja.
     *
     * Antes se hacia save(usuario), que reescribe TODAS las columnas del
     * usuario solo para cambiar una fecha.
     *
     * @return cuantas filas se actualizaron (1 si el usuario existe)
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Usuario u SET u.ultimoAcceso = :fecha WHERE u.idUsuario = :id")
    int actualizarUltimoAcceso(@Param("id") Integer id, @Param("fecha") LocalDateTime fecha);

    @Override
    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findAll(Pageable pageable);

    /**
     * Busqueda paginada por nombres, apellidos, DNI, correo, telefono o rol.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @EntityGraph(attributePaths = "rol")
    @Query("""
        SELECT u FROM Usuario u LEFT JOIN u.rol r
         WHERE LOWER(u.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(u.dni) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(u.telefono) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(r.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Usuario> buscar(@Param("texto") String texto, Pageable pageable);
}
