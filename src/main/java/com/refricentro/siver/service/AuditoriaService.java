package com.refricentro.siver.service;

import com.refricentro.siver.model.Auditoria;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Bitacora de acciones de los usuarios: se escribe sola y solo se consulta. */
public interface AuditoriaService {

    /** Guarda un registro. Nunca lanza excepciones: la auditoria no debe tumbar la peticion. */
    void registrar(Auditoria auditoria);

    Page<Auditoria> buscar(String texto, String modulo, String accion,
                           LocalDateTime desde, LocalDateTime hasta, Pageable pageable);
}
