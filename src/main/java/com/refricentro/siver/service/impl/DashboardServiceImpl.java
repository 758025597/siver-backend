package com.refricentro.siver.service.impl;

import static com.refricentro.siver.security.Roles.ADMIN;
import static com.refricentro.siver.security.Roles.TECNICO;
import static com.refricentro.siver.security.Roles.VENDEDOR;

import com.refricentro.siver.dto.DashboardResponse;
import com.refricentro.siver.dto.DashboardResponse.Etiqueta;
import com.refricentro.siver.dto.DashboardResponse.PuntoSerie;
import com.refricentro.siver.dto.DashboardResponse.StockBajo;
import com.refricentro.siver.dto.DashboardResponse.TopProducto;
import com.refricentro.siver.model.Venta;
import com.refricentro.siver.service.DashboardService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Calcula los indicadores del dashboard con consultas JPQL de agregacion
 * (COUNT, SUM, GROUP BY). Todo se calcula en MySQL: viajan solo los
 * totales, no las tablas completas.
 *
 * Las fechas ("hoy", "inicio de mes") se calculan en Java con la zona de
 * Lima y se mandan como parametros, asi no dependen de la hora del servidor
 * de base de datos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final Venta.EstadoVenta EMITIDA = Venta.EstadoVenta.EMITIDA;

    private final EntityManager em;

    @Override
    public DashboardResponse resumen(String rol) {
        boolean veVentas = ADMIN.equals(rol) || VENDEDOR.equals(rol);
        boolean veOrdenes = ADMIN.equals(rol) || TECNICO.equals(rol);

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        LocalDateTime manana = hoy.plusDays(1).atStartOfDay();

        Long ventasHoy = null, ventasMes = null;
        BigDecimal montoHoy = null, montoMes = null, ticket = null;
        List<PuntoSerie> serie = List.of();
        List<Etiqueta> porMetodo = List.of();
        List<TopProducto> top = List.of();
        if (veVentas) {
            Object[] dia = totalVentas(inicioHoy, manana);
            Object[] mes = totalVentas(inicioMes, manana);
            ventasHoy = (Long) dia[0];
            montoHoy = (BigDecimal) dia[1];
            ventasMes = (Long) mes[0];
            montoMes = (BigDecimal) mes[1];
            ticket = ventasMes == 0 ? BigDecimal.ZERO
                    : montoMes.divide(BigDecimal.valueOf(ventasMes), 2, RoundingMode.HALF_UP);
            serie = ultimos7Dias(hoy);
            porMetodo = ventasPorMetodo(inicioMes, manana);
            top = masVendidos(inicioMes, manana);
        }

        Long ordenesAbiertas = null, ordenesVencidas = null, finalizadasMes = null;
        List<Etiqueta> porEstado = List.of();
        if (veOrdenes) {
            ordenesAbiertas = contar("SELECT COUNT(o) FROM OrdenServicio o WHERE o.estado.esFinal = false");
            ordenesVencidas = em.createQuery("""
                    SELECT COUNT(o) FROM OrdenServicio o
                     WHERE o.estado.esFinal = false AND o.fechaEntregaEstimada < :hoy
                    """, Long.class).setParameter("hoy", hoy).getSingleResult();
            finalizadasMes = em.createQuery("""
                    SELECT COUNT(o) FROM OrdenServicio o
                     WHERE o.estado.esFinal = true AND LOWER(o.estado.nombre) <> 'cancelado'
                       AND o.fechaEntregaReal >= :inicio
                    """, Long.class).setParameter("inicio", inicioMes).getSingleResult();
            porEstado = em.createQuery("""
                    SELECT e.nombre, COUNT(o) FROM OrdenServicio o JOIN o.estado e
                     GROUP BY e.nombre, e.ordenFlujo ORDER BY e.ordenFlujo
                    """, Object[].class).getResultList().stream()
                    .map(f -> new Etiqueta((String) f[0], BigDecimal.valueOf((Long) f[1])))
                    .toList();
        }

        List<StockBajo> stockBajo = em.createQuery("""
                SELECT p.idProducto, p.codigo, p.nombre, p.stock, p.stockMinimo FROM Producto p
                 WHERE p.activo = true AND p.stock <= p.stockMinimo
                 ORDER BY p.stock ASC, p.nombre ASC
                """, Object[].class).setMaxResults(8).getResultList().stream()
                .map(f -> new StockBajo((Integer) f[0], (String) f[1], (String) f[2], (Integer) f[3], (Integer) f[4]))
                .toList();

        BigDecimal valorInventario = veVentas ? em.createQuery("""
                SELECT COALESCE(SUM(p.stock * p.precioCompra), 0) FROM Producto p WHERE p.activo = true
                """, BigDecimal.class).getSingleResult() : null;

        return new DashboardResponse(
                rol,
                ventasHoy, montoHoy, ventasMes, montoMes, ticket, serie, porMetodo, top,
                contar("SELECT COUNT(p) FROM Producto p WHERE p.activo = true"),
                contar("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo"),
                valorInventario,
                stockBajo,
                contar("SELECT COUNT(c) FROM Cliente c WHERE c.activo = true"),
                ordenesAbiertas, ordenesVencidas, finalizadasMes, porEstado);
    }

    /** [cantidad, monto] de las ventas emitidas en el rango [desde, hasta). */
    private Object[] totalVentas(LocalDateTime desde, LocalDateTime hasta) {
        return em.createQuery("""
                SELECT COUNT(v), COALESCE(SUM(v.total), 0) FROM Venta v
                 WHERE v.estado = :estado AND v.fechaVenta >= :desde AND v.fechaVenta < :hasta
                """, Object[].class)
                .setParameter("estado", EMITIDA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult();
    }

    /**
     * Ventas de los ultimos 7 dias, incluidos los dias sin ventas (en 0).
     * Se traen las ventas del rango y se agrupan por dia en Java: asi no se
     * depende de funciones de fecha propias de MySQL.
     */
    private List<PuntoSerie> ultimos7Dias(LocalDate hoy) {
        LocalDate inicio = hoy.minusDays(6);
        List<Object[]> filas = em.createQuery("""
                SELECT v.fechaVenta, v.total FROM Venta v
                 WHERE v.estado = :estado AND v.fechaVenta >= :desde AND v.fechaVenta < :hasta
                """, Object[].class)
                .setParameter("estado", EMITIDA)
                .setParameter("desde", inicio.atStartOfDay())
                .setParameter("hasta", hoy.plusDays(1).atStartOfDay())
                .getResultList();

        Map<LocalDate, long[]> cantidades = new HashMap<>();
        Map<LocalDate, BigDecimal> montos = new HashMap<>();
        for (Object[] f : filas) {
            LocalDate dia = ((LocalDateTime) f[0]).toLocalDate();
            cantidades.computeIfAbsent(dia, d -> new long[1])[0]++;
            montos.merge(dia, (BigDecimal) f[1], BigDecimal::add);
        }

        List<PuntoSerie> serie = new ArrayList<>();
        for (LocalDate d = inicio; !d.isAfter(hoy); d = d.plusDays(1)) {
            serie.add(new PuntoSerie(d,
                    cantidades.getOrDefault(d, new long[1])[0],
                    montos.getOrDefault(d, BigDecimal.ZERO)));
        }
        return serie;
    }

    private List<Etiqueta> ventasPorMetodo(LocalDateTime desde, LocalDateTime hasta) {
        return em.createQuery("""
                SELECT v.metodoPago, SUM(v.total) FROM Venta v
                 WHERE v.estado = :estado AND v.fechaVenta >= :desde AND v.fechaVenta < :hasta
                 GROUP BY v.metodoPago ORDER BY SUM(v.total) DESC
                """, Object[].class)
                .setParameter("estado", EMITIDA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList().stream()
                .map(f -> new Etiqueta(String.valueOf(f[0]), (BigDecimal) f[1]))
                .toList();
    }

    private List<TopProducto> masVendidos(LocalDateTime desde, LocalDateTime hasta) {
        return em.createQuery("""
                SELECT p.codigo, p.nombre, SUM(d.cantidad), SUM(d.subtotal)
                  FROM DetalleVenta d JOIN d.venta v JOIN d.producto p
                 WHERE v.estado = :estado AND v.fechaVenta >= :desde AND v.fechaVenta < :hasta
                 GROUP BY p.idProducto, p.codigo, p.nombre
                 ORDER BY SUM(d.cantidad) DESC
                """, Object[].class)
                .setParameter("estado", EMITIDA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .setMaxResults(5)
                .getResultList().stream()
                .map(f -> new TopProducto((String) f[0], (String) f[1],
                        ((Number) f[2]).longValue(), (BigDecimal) f[3]))
                .toList();
    }

    private Long contar(String jpql) {
        return em.createQuery(jpql, Long.class).getSingleResult();
    }
}
