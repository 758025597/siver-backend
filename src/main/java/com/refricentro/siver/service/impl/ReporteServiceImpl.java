package com.refricentro.siver.service.impl;

import com.refricentro.siver.dto.TablaReporte;
import com.refricentro.siver.dto.TablaReporte.Columna;
import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.model.MovimientoInventario;
import com.refricentro.siver.model.OrdenServicio;
import com.refricentro.siver.model.Producto;
import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.model.Venta;
import com.refricentro.siver.reporte.ExportadorPdf;
import com.refricentro.siver.repository.DetalleVentaRepository;
import com.refricentro.siver.repository.VentaRepository;
import com.refricentro.siver.security.UsuarioActual;
import com.refricentro.siver.service.ReporteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Arma los reportes con consultas JPQL dinamicas.
 *
 * "Dinamicas" quiere decir que cada filtro se agrega a la consulta solo si
 * llega. Pero los VALORES siempre viajan como parametros nombrados
 * (:desde, :estado...), nunca pegados al texto: no hay inyeccion SQL posible.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DIA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EntityManager em;
    private final UsuarioActual usuarioActual;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ExportadorPdf exportadorPdf;

    // ------------------------------------------------------------------ ventas

    @Override
    public TablaReporte ventas(LocalDate desde, LocalDate hasta, String estado,
                               Integer idUsuario, String metodoPago) {
        LocalDate[] rango = rango(desde, hasta);
        Consulta q = new Consulta("""
                SELECT v FROM Venta v JOIN FETCH v.cliente JOIN FETCH v.usuario
                 WHERE v.fechaVenta >= :desde AND v.fechaVenta < :hasta""");
        q.param("desde", rango[0].atStartOfDay());
        q.param("hasta", rango[1].plusDays(1).atStartOfDay());
        List<String> filtros = new ArrayList<>(List.of(periodo(rango)));

        if (estado != null && !estado.isBlank()) {
            q.and("v.estado = :estado", "estado", enumValor(Venta.EstadoVenta.class, estado, "estado"));
            filtros.add("Estado: " + estado);
        }
        if (idUsuario != null) {
            q.and("v.usuario.idUsuario = :idUsuario", "idUsuario", idUsuario);
            filtros.add("Vendedor: " + nombreUsuario(idUsuario));
        }
        if (metodoPago != null && !metodoPago.isBlank()) {
            q.and("v.metodoPago = :metodo", "metodo", enumValor(Venta.MetodoPago.class, metodoPago, "metodoPago"));
            filtros.add("Metodo de pago: " + metodoPago);
        }
        List<Venta> ventas = q.ordenar("v.fechaVenta").lista(Venta.class);

        List<List<Object>> filas = new ArrayList<>();
        BigDecimal sub = BigDecimal.ZERO, igv = BigDecimal.ZERO, total = BigDecimal.ZERO;
        int emitidas = 0;
        for (Venta v : ventas) {
            filas.add(fila(v.getFechaVenta().format(DIA_HORA),
                    v.getTipoComprobante() + " " + v.getNumeroComprobante(),
                    v.getCliente().getNombres(),
                    v.getUsuario().getNombres() + " " + v.getUsuario().getApellidos(),
                    v.getMetodoPago().name(),
                    v.getEstado().name(),
                    v.getSubtotal(), v.getIgv(), v.getTotal()));
            if (v.getEstado() == Venta.EstadoVenta.EMITIDA) {
                emitidas++;
                sub = sub.add(v.getSubtotal());
                igv = igv.add(v.getIgv());
                total = total.add(v.getTotal());
            }
        }

        return reporte("Reporte de ventas", filtros,
                List.of(Columna.texto("Fecha"), Columna.texto("Comprobante"), Columna.texto("Cliente"),
                        Columna.texto("Vendedor"), Columna.texto("Pago"), Columna.texto("Estado"),
                        Columna.moneda("Subtotal"), Columna.moneda("IGV"), Columna.moneda("Total")),
                filas,
                fila("TOTAL (" + emitidas + " emitidas)", "", "", "", "", "", sub, igv, total));
    }

    // -------------------------------------------------------------- inventario

    @Override
    public TablaReporte inventario(Integer idCategoria, boolean soloStockBajo, boolean soloSinRotacion, int dias) {
        int periodo = Math.max(1, Math.min(dias, 365));
        Consulta q = new Consulta("""
                SELECT p FROM Producto p JOIN FETCH p.categoria c WHERE p.activo = true""");
        List<String> filtros = new ArrayList<>();
        if (idCategoria != null) {
            q.and("c.idCategoria = :idCategoria", "idCategoria", idCategoria);
        }
        if (soloStockBajo) {
            q.and("p.stock <= p.stockMinimo", null, null);
            filtros.add("Solo stock bajo");
        }
        List<Producto> productos = q.ordenar("c.nombre, p.nombre").lista(Producto.class);
        if (idCategoria != null) {
            filtros.add(0, "Categoria: " + (productos.isEmpty() ? "#" + idCategoria
                    : productos.get(0).getCategoria().getNombre()));
        }

        // Unidades vendidas por producto en el periodo: mide la ROTACION.
        Map<Integer, Long> vendidos = new HashMap<>();
        em.createQuery("""
                SELECT d.producto.idProducto, SUM(d.cantidad) FROM DetalleVenta d
                 WHERE d.venta.estado = :estado AND d.venta.fechaVenta >= :desde
                 GROUP BY d.producto.idProducto
                """, Object[].class)
                .setParameter("estado", Venta.EstadoVenta.EMITIDA)
                .setParameter("desde", LocalDate.now().minusDays(periodo).atStartOfDay())
                .getResultList()
                .forEach(f -> vendidos.put((Integer) f[0], ((Number) f[1]).longValue()));

        List<List<Object>> filas = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;
        long unidades = 0;
        for (Producto p : productos) {
            long vendido = vendidos.getOrDefault(p.getIdProducto(), 0L);
            if (soloSinRotacion && vendido > 0) {
                continue;
            }
            BigDecimal valor = p.getPrecioCompra().multiply(BigDecimal.valueOf(p.getStock()));
            String situacion = p.getStock() == 0 ? "Agotado"
                    : p.getStock() <= p.getStockMinimo() ? "Stock bajo"
                    : vendido == 0 ? "Sin rotacion" : "Normal";
            filas.add(fila(p.getCodigo(), p.getNombre(), p.getCategoria().getNombre(),
                    p.getUnidadMedida().name(), p.getStock(), p.getStockMinimo(),
                    p.getPrecioCompra(), p.getPrecioVenta(), valor, vendido, situacion));
            valorTotal = valorTotal.add(valor);
            unidades += p.getStock();
        }
        if (soloSinRotacion) {
            filtros.add("Solo productos sin ventas");
        }
        filtros.add("Rotacion: unidades vendidas en los ultimos " + periodo + " dias");

        return reporte("Inventario valorizado y rotacion", filtros,
                List.of(Columna.texto("Codigo"), Columna.texto("Producto"), Columna.texto("Categoria"),
                        Columna.texto("Unidad"), Columna.entero("Stock"), Columna.entero("Minimo"),
                        Columna.moneda("P. compra"), Columna.moneda("P. venta"), Columna.moneda("Valor stock"),
                        Columna.entero("Vendidos"), Columna.texto("Situacion")),
                filas,
                fila("TOTAL", filas.size() + " productos", "", "", unidades, "", "", "", valorTotal, "", ""));
    }

    // ------------------------------------------------------------------ kardex

    @Override
    public TablaReporte kardex(Integer idProducto, LocalDate desde, LocalDate hasta) {
        if (idProducto == null) {
            throw new IllegalArgumentException("Elige el producto del kardex.");
        }
        Producto producto = em.find(Producto.class, idProducto);
        if (producto == null) {
            throw new RecursoNoEncontradoException("Producto", idProducto);
        }
        LocalDate[] rango = rango(desde, hasta);

        List<MovimientoInventario> movimientos = em.createQuery("""
                SELECT m FROM MovimientoInventario m LEFT JOIN FETCH m.usuario
                 WHERE m.producto.idProducto = :id
                   AND m.fechaMovimiento >= :desde AND m.fechaMovimiento < :hasta
                 ORDER BY m.fechaMovimiento, m.idMovimiento
                """, MovimientoInventario.class)
                .setParameter("id", idProducto)
                .setParameter("desde", rango[0].atStartOfDay())
                .setParameter("hasta", rango[1].plusDays(1).atStartOfDay())
                .getResultList();

        List<List<Object>> filas = new ArrayList<>();
        long entradas = 0, salidas = 0;
        for (MovimientoInventario m : movimientos) {
            int variacion = m.getStockNuevo() - m.getStockAnterior();
            Usuario u = m.getUsuario();
            filas.add(fila(m.getFechaMovimiento().format(DIA_HORA), m.getTipoMovimiento().name(),
                    m.getMotivo() == null ? "" : m.getMotivo(),
                    m.getStockAnterior(),
                    variacion > 0 ? variacion : 0,
                    variacion < 0 ? -variacion : 0,
                    m.getStockNuevo(),
                    u == null ? "Sistema" : u.getNombres() + " " + u.getApellidos()));
            if (variacion > 0) {
                entradas += variacion;
            } else {
                salidas += -variacion;
            }
        }
        Object saldoFinal = movimientos.isEmpty() ? producto.getStock()
                : movimientos.get(movimientos.size() - 1).getStockNuevo();

        return reporte("Kardex de " + producto.getCodigo() + " - " + producto.getNombre(),
                List.of(periodo(rango), "Stock actual: " + producto.getStock()
                        + " " + producto.getUnidadMedida().name().toLowerCase()),
                List.of(Columna.texto("Fecha"), Columna.texto("Tipo"), Columna.texto("Motivo"),
                        Columna.entero("Saldo anterior"), Columna.entero("Entrada"), Columna.entero("Salida"),
                        Columna.entero("Saldo"), Columna.texto("Usuario")),
                filas,
                fila("TOTALES", "", "", "", entradas, salidas, saldoFinal, ""));
    }

    // ----------------------------------------------------------------- ordenes

    @Override
    public TablaReporte ordenes(LocalDate desde, LocalDate hasta, Integer idEstado, Integer idTecnico) {
        LocalDate[] rango = rango(desde, hasta);
        Consulta q = new Consulta("""
                SELECT o FROM OrdenServicio o JOIN FETCH o.cliente JOIN FETCH o.tecnico JOIN FETCH o.estado e
                 WHERE o.fechaIngreso >= :desde AND o.fechaIngreso < :hasta""");
        q.param("desde", rango[0].atStartOfDay());
        q.param("hasta", rango[1].plusDays(1).atStartOfDay());
        List<String> filtros = new ArrayList<>(List.of(periodo(rango)));
        if (idEstado != null) {
            q.and("e.idEstado = :idEstado", "idEstado", idEstado);
        }
        if (idTecnico != null) {
            q.and("o.tecnico.idUsuario = :idTecnico", "idTecnico", idTecnico);
            filtros.add("Tecnico: " + nombreUsuario(idTecnico));
        }
        List<OrdenServicio> ordenes = q.ordenar("o.fechaIngreso").lista(OrdenServicio.class);
        if (idEstado != null && !ordenes.isEmpty()) {
            filtros.add("Estado: " + ordenes.get(0).getEstado().getNombre());
        }

        List<List<Object>> filas = new ArrayList<>();
        BigDecimal estimado = BigDecimal.ZERO, cobrado = BigDecimal.ZERO;
        for (OrdenServicio o : ordenes) {
            filas.add(fila(o.getNumeroOrden(), o.getFechaIngreso().format(DIA_HORA),
                    o.getCliente().getNombres(),
                    o.getEquipo() + (o.getMarca() == null ? "" : " " + o.getMarca()),
                    o.getTecnico().getNombres() + " " + o.getTecnico().getApellidos(),
                    o.getEstado().getNombre(),
                    o.getFechaEntregaEstimada() == null ? "" : o.getFechaEntregaEstimada().format(DIA),
                    o.getCostoEstimado(),
                    o.getCostoFinal() == null ? BigDecimal.ZERO : o.getCostoFinal()));
            estimado = estimado.add(o.getCostoEstimado());
            if (o.getCostoFinal() != null) {
                cobrado = cobrado.add(o.getCostoFinal());
            }
        }

        return reporte("Ordenes de servicio", filtros,
                List.of(Columna.texto("N° orden"), Columna.texto("Ingreso"), Columna.texto("Cliente"),
                        Columna.texto("Equipo"), Columna.texto("Tecnico"), Columna.texto("Estado"),
                        Columna.texto("Entrega est."), Columna.moneda("Costo est."), Columna.moneda("Costo final")),
                filas,
                fila("TOTAL (" + filas.size() + ")", "", "", "", "", "", "", estimado, cobrado));
    }

    // ------------------------------------------------------------- comprobante

    @Override
    public byte[] comprobante(Integer idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", idVenta));
        return exportadorPdf.comprobante(venta, detalleVentaRepository.findByVenta_IdVenta(idVenta));
    }

    // ----------------------------------------------------------------- ayudas

    private TablaReporte reporte(String titulo, List<String> filtros, List<Columna> columnas,
                                 List<List<Object>> filas, List<Object> totales) {
        return new TablaReporte(titulo, String.join("  ·  ", filtros), LocalDateTime.now(),
                usuarioActual.correo().orElse("sistema"), columnas, filas, totales);
    }

    /** Por defecto: del primer dia del mes hasta hoy. */
    private static LocalDate[] rango(LocalDate desde, LocalDate hasta) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.withDayOfMonth(1);
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        return new LocalDate[] {inicio, fin};
    }

    private static String periodo(LocalDate[] rango) {
        return "Del " + rango[0].format(DIA) + " al " + rango[1].format(DIA);
    }

    private String nombreUsuario(Integer id) {
        Usuario u = em.find(Usuario.class, id);
        return u == null ? "#" + id : u.getNombres() + " " + u.getApellidos();
    }

    private static <E extends Enum<E>> E enumValor(Class<E> tipo, String valor, String campo) {
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor no valido para '" + campo + "': " + valor);
        }
    }

    private static List<Object> fila(Object... celdas) {
        return Arrays.asList(celdas);
    }

    /** Pequeño armador de consultas JPQL con filtros opcionales y parametros nombrados. */
    private final class Consulta {
        private final StringBuilder jpql;
        private final Map<String, Object> parametros = new HashMap<>();

        Consulta(String base) {
            this.jpql = new StringBuilder(base);
        }

        void param(String nombre, Object valor) {
            parametros.put(nombre, valor);
        }

        void and(String condicion, String nombre, Object valor) {
            jpql.append(" AND ").append(condicion);
            if (nombre != null) {
                parametros.put(nombre, valor);
            }
        }

        Consulta ordenar(String orden) {
            jpql.append(" ORDER BY ").append(orden);
            return this;
        }

        <T> List<T> lista(Class<T> tipo) {
            TypedQuery<T> query = em.createQuery(jpql.toString(), tipo);
            parametros.forEach(query::setParameter);
            return query.getResultList();
        }
    }
}
