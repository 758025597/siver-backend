package com.refricentro.siver.repository;

import com.refricentro.siver.model.Notificacion;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    /**
     * Las notificaciones que le tocan a un rol: las dirigidas a ese rol y las
     * que van para todos (roles_destino null). Se guarda "ADMINISTRADOR,VENDEDOR",
     * por eso se compara con comas a los lados: ",ADMINISTRADOR,VENDEDOR,"
     * contiene ",VENDEDOR," pero no ",VENDE,".
     */
    @Query("""
        SELECT n FROM Notificacion n
         WHERE n.fecha >= :desde
           AND (n.rolesDestino IS NULL
                OR CONCAT(',', n.rolesDestino, ',') LIKE CONCAT('%,', :rol, ',%'))
        """)
    Page<Notificacion> paraRol(@Param("rol") String rol,
                               @Param("desde") LocalDateTime desde,
                               Pageable pageable);

    /** Cuantas de esas todavia no leyo el usuario (el numero de la campanita). */
    @Query("""
        SELECT COUNT(n) FROM Notificacion n
         WHERE n.fecha >= :desde
           AND (n.rolesDestino IS NULL
                OR CONCAT(',', n.rolesDestino, ',') LIKE CONCAT('%,', :rol, ',%'))
           AND NOT EXISTS (SELECT l FROM NotificacionLectura l
                            WHERE l.notificacion = n AND l.usuario.idUsuario = :idUsuario)
        """)
    long contarNoLeidas(@Param("rol") String rol,
                        @Param("idUsuario") Integer idUsuario,
                        @Param("desde") LocalDateTime desde);

    /** Evita repetir el mismo aviso (mismo tipo y mismo objeto) en poco tiempo. */
    boolean existsByTipoAndReferenciaAndFechaAfter(String tipo, String referencia, LocalDateTime fecha);
}
