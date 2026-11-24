
package com.refricentro.siver.service.impl;

import com.refricentro.siver.security.Roles;
import com.refricentro.siver.service.NotificacionService;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.model.EstadoOrden;
import com.refricentro.siver.model.OrdenServicio;
import com.refricentro.siver.repository.EstadoOrdenRepository;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.repository.OrdenServicioRepository;
import com.refricentro.siver.service.OrdenServicioService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenServicioServiceImpl
        extends ServicioGenerico<OrdenServicio, Integer>
        implements OrdenServicioService {

    private static final String ESTADO_CANCELADO = "Cancelado";

    private final OrdenServicioRepository ordenServicioRepository;
    private final EstadoOrdenRepository estadoOrdenRepository;
    private final NotificacionService notificacionService;

    public OrdenServicioServiceImpl(
            OrdenServicioRepository ordenServicioRepository,
            EstadoOrdenRepository estadoOrdenRepository,
            NotificacionService notificacionService) {
        this.ordenServicioRepository = ordenServicioRepository;
        this.estadoOrdenRepository = estadoOrdenRepository;
        this.notificacionService = notificacionService;
    }

    @Override
    protected IGenericRepository<OrdenServicio, Integer> getRepo() {
        return ordenServicioRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "OrdenServicio";
    }

    @Override
    protected void copiarDatos(OrdenServicio origen, OrdenServicio destino) {
        destino.setCliente(origen.getCliente());
        destino.setTecnico(origen.getTecnico());
        destino.setEquipo(origen.getEquipo());
        destino.setMarca(origen.getMarca());
        destino.setModelo(origen.getModelo());
        destino.setNumeroSerie(origen.getNumeroSerie());
        destino.setDescripcionFalla(origen.getDescripcionFalla());
        destino.setDiagnostico(origen.getDiagnostico());
        destino.setTrabajoRealizado(origen.getTrabajoRealizado());
        destino.setFechaEntregaEstimada(origen.getFechaEntregaEstimada());
        destino.setCostoEstimado(origen.getCostoEstimado());
        destino.setCostoFinal(origen.getCostoFinal());
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        OrdenServicio orden = buscarPorId(id);
        orden.setEstado(estadoCancelado());
        ordenServicioRepository.save(orden);
    }

    @Override
    @Transactional
    public OrdenServicio cambiarEstado(Integer idOrden, Integer idEstado) {
        OrdenServicio orden = buscarPorId(idOrden);
        EstadoOrden estado = estadoOrdenRepository.findById(idEstado)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("EstadoOrden", idEstado));
        orden.setEstado(estado);
        OrdenServicio guardada = ordenServicioRepository.save(orden);

        // Un estado final que no sea "Cancelado" = el equipo esta listo.
        if (Boolean.TRUE.equals(estado.getEsFinal())
                && !ESTADO_CANCELADO.equalsIgnoreCase(estado.getNombre())) {
            notificacionService.notificar("ORDEN_FINALIZADA", "Equipo listo para entregar",
                    "La orden %s (%s de %s) paso a '%s'. Avisa al cliente para el recojo."
                            .formatted(orden.getNumeroOrden(), orden.getEquipo(),
                                    orden.getCliente().getNombres(), estado.getNombre()),
                    "/pages/ordenes-servicio", "orden-fin:" + idOrden,
                    Roles.ADMIN, Roles.VENDEDOR, Roles.TECNICO);
        }
        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenServicio> listarPorCliente(Integer idCliente) {
        return ordenServicioRepository.findByCliente_IdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenServicio> listarPorTecnico(Integer idUsuario) {
        return ordenServicioRepository.findByTecnico_IdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenServicio> listarPorEstado(Integer idEstado) {
        return ordenServicioRepository.findByEstado_IdEstado(idEstado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenServicio> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Las fechas 'desde' y 'hasta' son obligatorias.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);

        return ordenServicioRepository.findByRangoFechasIngreso(inicio, fin);
    }

    private EstadoOrden estadoCancelado() {
        return estadoOrdenRepository.findByNombreIgnoreCase(ESTADO_CANCELADO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "EstadoOrden con nombre", ESTADO_CANCELADO));
    }

    /** Busqueda paginada con la consulta JPQL del repositorio. */
    @Override
    protected Page<OrdenServicio> buscar(String texto, Pageable pageable) {
        return ordenServicioRepository.buscar(texto, pageable);
    }
}
