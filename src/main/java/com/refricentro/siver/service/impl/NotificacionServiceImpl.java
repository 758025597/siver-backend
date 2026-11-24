package com.refricentro.siver.service.impl;

import static com.refricentro.siver.security.Roles.ADMIN;
import static com.refricentro.siver.security.Roles.TECNICO;
import static com.refricentro.siver.security.Roles.VENDEDOR;

import com.refricentro.siver.dto.NotificacionResponse;
import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.model.Notificacion;
import com.refricentro.siver.model.NotificacionLectura;
import com.refricentro.siver.model.NotificacionLecturaId;
import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.repository.NotificacionLecturaRepository;
import com.refricentro.siver.repository.NotificacionRepository;
import com.refricentro.siver.security.Roles;
import com.refricentro.siver.security.UsuarioActual;
import com.refricentro.siver.service.NotificacionService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    /** No se repite el mismo aviso (mismo tipo y objeto) dentro de este plazo. */
    private static final int HORAS_SIN_REPETIR = 24;
    /** La campanita muestra los avisos de los ultimos 30 dias. */
    private static final int DIAS_VISIBLES = 30;

    private final NotificacionRepository repositorio;
    private final NotificacionLecturaRepository lecturaRepositorio;
    private final UsuarioActual usuarioActual;
    private final CorreoNotificador correo;
    private final EntityManager em;

    @Override
    @Transactional
    public void notificar(String tipo, String titulo, String mensaje, String enlace,
                          String referencia, String... roles) {
        if (referencia != null && repositorio.existsByTipoAndReferenciaAndFechaAfter(
                tipo, referencia, LocalDateTime.now().minusHours(HORAS_SIN_REPETIR))) {
            return;
        }
        Notificacion n = new Notificacion();
        n.setTipo(tipo);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setEnlace(enlace);
        n.setReferencia(referencia);
        n.setRolesDestino(roles.length == 0 ? null : String.join(",", roles));
        n.setFecha(LocalDateTime.now());
        repositorio.save(n);

        correo.enviar(titulo, mensaje, roles);
    }

    /**
     * La consulta pide columnas sueltas (no la entidad Producto) a proposito:
     * asi lee el stock REAL de la base. Si pidiera la entidad, Hibernate podria
     * devolver la copia que ya tiene en memoria, de antes de que el trigger de
     * la venta descontara el stock.
     */
    @Override
    @Transactional
    public void verificarStockBajo(Collection<Integer> idsProductos) {
        if (idsProductos == null || idsProductos.isEmpty()) {
            return;
        }
        List<Object[]> filas = em.createQuery("""
                SELECT p.idProducto, p.codigo, p.nombre, p.stock, p.stockMinimo
                  FROM Producto p
                 WHERE p.activo = true AND p.stock <= p.stockMinimo AND p.idProducto IN :ids
                """, Object[].class)
                .setParameter("ids", idsProductos)
                .getResultList();
        for (Object[] f : filas) {
            avisarStockBajo((Integer) f[0], (String) f[1], (String) f[2], (Integer) f[3], (Integer) f[4]);
        }
    }

    private void avisarStockBajo(Integer id, String codigo, String nombre, Integer stock, Integer minimo) {
        String titulo = stock == 0 ? "Producto agotado" : "Stock bajo";
        notificar("STOCK_BAJO", titulo,
                "%s (%s) tiene %d unidad(es); el minimo es %d. Conviene reponerlo."
                        .formatted(nombre, codigo, stock, minimo),
                "/pages/movimientos?producto=" + id, "producto:" + id, ADMIN, VENDEDOR);
    }

    /**
     * Revision automatica: todos los dias a las 8:00 y una vez al arrancar el
     * servidor. Avisa productos con stock bajo y ordenes con la fecha de
     * entrega vencida (los "vencimientos" que pide la especificacion).
     */
    @Override
    @Scheduled(cron = "0 0 8 * * *", zone = "America/Lima")
    @Transactional
    public void revisionGeneral() {
        List<Object[]> productos = em.createQuery("""
                SELECT p.idProducto, p.codigo, p.nombre, p.stock, p.stockMinimo
                  FROM Producto p
                 WHERE p.activo = true AND p.stock <= p.stockMinimo
                """, Object[].class).getResultList();
        for (Object[] f : productos) {
            avisarStockBajo((Integer) f[0], (String) f[1], (String) f[2], (Integer) f[3], (Integer) f[4]);
        }

        List<Object[]> ordenes = em.createQuery("""
                SELECT o.idOrden, o.numeroOrden, o.equipo, o.fechaEntregaEstimada, c.nombres
                  FROM OrdenServicio o JOIN o.estado e JOIN o.cliente c
                 WHERE e.esFinal = false AND o.fechaEntregaEstimada < :hoy
                """, Object[].class)
                .setParameter("hoy", LocalDate.now())
                .getResultList();
        for (Object[] f : ordenes) {
            notificar("ORDEN_VENCIDA", "Orden vencida",
                    "La orden %s (%s de %s) debia entregarse el %s y sigue abierta."
                            .formatted(f[1], f[2], f[4],
                                    ((LocalDate) f[3]).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                    "/pages/ordenes-servicio/edit/" + f[0], "orden:" + f[0], ADMIN, TECNICO);
        }
        log.info("Revision de notificaciones: {} producto(s) con stock bajo, {} orden(es) vencida(s)",
                productos.size(), ordenes.size());
    }

    /** Tambien corre al iniciar, para no esperar hasta las 8:00 del dia siguiente. */
    @EventListener(ApplicationReadyEvent.class)
    public void alIniciar() {
        try {
            revisionGeneral();
        } catch (RuntimeException e) {
            log.warn("No se pudo hacer la revision inicial de notificaciones: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> listarMias(Pageable pageable) {
        Usuario usuario = usuarioActualObligatorio();
        Pageable orden = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "fecha"));
        Page<Notificacion> pagina = repositorio.paraRol(rolDe(usuario), desde(), orden);

        List<Integer> ids = pagina.getContent().stream().map(Notificacion::getIdNotificacion).toList();
        Set<Integer> leidas = ids.isEmpty() ? Set.of()
                : new HashSet<>(lecturaRepositorio.leidasDe(usuario.getIdUsuario(), ids));

        List<NotificacionResponse> contenido = pagina.getContent().stream()
                .map(n -> new NotificacionResponse(n.getIdNotificacion(), n.getTipo(), n.getTitulo(),
                        n.getMensaje(), n.getEnlace(), n.getFecha(), leidas.contains(n.getIdNotificacion())))
                .toList();
        return new PageImpl<>(contenido, orden, pagina.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas() {
        Usuario usuario = usuarioActualObligatorio();
        return repositorio.contarNoLeidas(rolDe(usuario), usuario.getIdUsuario(), desde());
    }

    @Override
    @Transactional
    public void marcarLeida(Integer idNotificacion) {
        Usuario usuario = usuarioActualObligatorio();
        Notificacion n = repositorio.findById(idNotificacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion", idNotificacion));
        NotificacionLecturaId id = new NotificacionLecturaId(idNotificacion, usuario.getIdUsuario());
        if (!lecturaRepositorio.existsById(id)) {
            lecturaRepositorio.save(new NotificacionLectura(n, usuario));
        }
    }

    @Override
    @Transactional
    public void marcarTodasLeidas() {
        Usuario usuario = usuarioActualObligatorio();
        Page<Notificacion> todas = repositorio.paraRol(rolDe(usuario), desde(), Pageable.unpaged());
        List<Integer> ids = todas.getContent().stream().map(Notificacion::getIdNotificacion).toList();
        if (ids.isEmpty()) {
            return;
        }
        Set<Integer> yaLeidas = new HashSet<>(lecturaRepositorio.leidasDe(usuario.getIdUsuario(), ids));
        for (Notificacion n : todas) {
            if (!yaLeidas.contains(n.getIdNotificacion())) {
                lecturaRepositorio.save(new NotificacionLectura(n, usuario));
            }
        }
    }

    private Usuario usuarioActualObligatorio() {
        return usuarioActual.obtener()
                .orElseThrow(() -> new IllegalArgumentException("No hay un usuario en sesion."));
    }

    private static String rolDe(Usuario u) {
        return Roles.normalizar(u.getRol() != null ? u.getRol().getNombre() : null);
    }

    private static LocalDateTime desde() {
        return LocalDateTime.now().minusDays(DIAS_VISIBLES);
    }
}
