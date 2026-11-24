package com.refricentro.siver.dto;

import com.refricentro.siver.model.Auditoria;
import java.time.LocalDateTime;

public record AuditoriaResponse(
        Long idAuditoria,
        LocalDateTime fecha,
        Integer idUsuario,
        String usuario,
        String rol,
        String accion,
        String modulo,
        String idRegistro,
        String metodo,
        String ruta,
        Integer estadoHttp,
        String ip) {

    public static AuditoriaResponse de(Auditoria a) {
        return new AuditoriaResponse(a.getIdAuditoria(), a.getFecha(), a.getIdUsuario(),
                a.getUsuario(), a.getRol(), a.getAccion(), a.getModulo(), a.getIdRegistro(),
                a.getMetodo(), a.getRuta(), a.getEstadoHttp(), a.getIp());
    }
}
