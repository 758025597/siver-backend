package com.refricentro.siver.service;

import com.refricentro.siver.dto.NotificacionResponse;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Notificaciones dentro del sistema (la campanita) y por correo.
 *
 * Eventos que generan un aviso:
 *   STOCK_BAJO        un producto llego a su stock minimo (venta o ajuste)
 *   VENTA_ANULADA     se anulo un comprobante y se devolvio el stock
 *   ORDEN_FINALIZADA  un equipo esta listo para entregar
 *   ORDEN_VENCIDA     paso la fecha de entrega estimada y la orden sigue abierta
 */
public interface NotificacionService {

    /** Crea el aviso si no se envio uno igual en las ultimas horas. Tambien manda el correo. */
    void notificar(String tipo, String titulo, String mensaje, String enlace,
                   String referencia, String... roles);

    /** Revisa estos productos y avisa los que quedaron en o bajo su stock minimo. */
    void verificarStockBajo(Collection<Integer> idsProductos);

    /** Revision completa: todos los productos con stock bajo y todas las ordenes vencidas. */
    void revisionGeneral();

    Page<NotificacionResponse> listarMias(Pageable pageable);

    long contarNoLeidas();

    void marcarLeida(Integer idNotificacion);

    void marcarTodasLeidas();
}
