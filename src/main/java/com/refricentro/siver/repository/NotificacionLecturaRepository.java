package com.refricentro.siver.repository;

import com.refricentro.siver.model.NotificacionLectura;
import com.refricentro.siver.model.NotificacionLecturaId;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** El tipo del id es la clave compuesta: JpaRepository<Entidad, NotificacionLecturaId>. */
@Repository
public interface NotificacionLecturaRepository
        extends JpaRepository<NotificacionLectura, NotificacionLecturaId> {

    /** De estas notificaciones, cuales ya leyo el usuario. */
    @Query("""
        SELECT l.id.idNotificacion FROM NotificacionLectura l
         WHERE l.id.idUsuario = :idUsuario AND l.id.idNotificacion IN :ids
        """)
    List<Integer> leidasDe(@Param("idUsuario") Integer idUsuario,
                           @Param("ids") Collection<Integer> ids);
}
