package com.refricentro.siver.service.impl;

import com.refricentro.siver.model.Auditoria;
import com.refricentro.siver.repository.AuditoriaRepository;
import com.refricentro.siver.service.AuditoriaService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository repositorio;

    /**
     * REQUIRES_NEW: el registro va en su PROPIA transaccion. Asi, si la
     * operacion auditada hace rollback, igual queda constancia del intento;
     * y si falla la auditoria, no se deshace la operacion del usuario.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(Auditoria auditoria) {
        try {
            if (auditoria.getFecha() == null) {
                auditoria.setFecha(LocalDateTime.now());
            }
            repositorio.save(auditoria);
        } catch (RuntimeException e) {
            log.warn("No se pudo guardar la auditoria de {} {}: {}",
                    auditoria.getMetodo(), auditoria.getRuta(), e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Auditoria> buscar(String texto, String modulo, String accion,
                                  LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        return repositorio.buscar(limpiar(texto), limpiar(modulo), limpiar(accion), desde, hasta, pageable);
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
