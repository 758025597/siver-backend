
package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.model.EstadoOrden;
import com.refricentro.siver.model.OrdenServicio;
import com.refricentro.siver.repository.EstadoOrdenRepository;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.repository.OrdenServicioRepository;
import com.refricentro.siver.service.OrdenServicioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenServicioServiceImpl
        extends ServicioGenerico<OrdenServicio, Integer>
        implements OrdenServicioService {

    private static final String ESTADO_CANCELADO = "Cancelado";

    private final OrdenServicioRepository ordenServicioRepository;
    private final EstadoOrdenRepository estadoOrdenRepository;

    public OrdenServicioServiceImpl(
            OrdenServicioRepository ordenServicioRepository,
            EstadoOrdenRepository estadoOrdenRepository) {
        this.ordenServicioRepository = ordenServicioRepository;
        this.estadoOrdenRepository = estadoOrdenRepository;
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
        return ordenServicioRepository.save(orden);
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

    private EstadoOrden estadoCancelado() {
        return estadoOrdenRepository.findByNombreIgnoreCase(ESTADO_CANCELADO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "EstadoOrden con nombre", ESTADO_CANCELADO));
    }
}
