package com.refricentro.siver.repository;

import com.refricentro.siver.model.Auditoria;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    /**
     * Consulta de la bitacora con filtros opcionales (null o '' = no filtra).
     * Parametros nombrados: nada se concatena, no hay inyeccion SQL posible.
     */
    @Query("""
        SELECT a FROM Auditoria a
         WHERE (:texto = '' OR LOWER(a.usuario) LIKE LOWER(CONCAT('%', :texto, '%'))
                            OR LOWER(a.ruta) LIKE LOWER(CONCAT('%', :texto, '%')))
           AND (:modulo = '' OR a.modulo = :modulo)
           AND (:accion = '' OR a.accion = :accion)
           AND (:desde IS NULL OR a.fecha >= :desde)
           AND (:hasta IS NULL OR a.fecha <= :hasta)
        """)
    Page<Auditoria> buscar(@Param("texto") String texto,
                           @Param("modulo") String modulo,
                           @Param("accion") String accion,
                           @Param("desde") LocalDateTime desde,
                           @Param("hasta") LocalDateTime hasta,
                           Pageable pageable);
}
