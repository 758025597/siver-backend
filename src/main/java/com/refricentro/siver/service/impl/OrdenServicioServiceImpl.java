package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.modelos.EstadoOrden;
import com.refricentro.siver.modelos.OrdenServicio;
import com.refricentro.siver.repository.EstadoOrdenRepository;
import com.refricentro.siver.repository.OrdenServicioRepository;
import com.refricentro.siver.service.OrdenServicioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de las ordenes de servicio tecnico.
 *
 * Hereda de ServicioGenerico porque orden_servicio NO tiene columna `activo`,
 * asi que no puede implementar Activable. Pero su borrado NO puede ser fisico:
 * una orden es el historial de atencion a un cliente.
 *
 * Por eso se sobrescribe eliminar(): en vez de borrar la fila, mueve la orden
 * al estado "Cancelado". Ese es el soft delete de esta entidad: su ciclo de
 * vida son los estados, no una bandera.
 */
@Service
public class OrdenServicioServiceImpl extends ServicioGenerico<OrdenServicio, Integer>
        implements OrdenServicioService {

    private static final String ESTADO_CANCELADO = "Cancelado";

    private final OrdenServicioRepository ordenServicioRepository;
    private final EstadoOrdenRepository estadoOrdenRepository;

    public OrdenServicioServiceImpl(OrdenServicioRepository ordenServicioRepository,
                                    EstadoOrdenRepository estadoOrdenRepository) {
        super(ordenServicioRepository);
        this.ordenServicioRepository = ordenServicioRepository;
        this.estadoOrdenRepository = estadoOrdenRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "OrdenServicio";
    }

    /**
     * Campos editables con un PUT.
     *
     * No se copian: el id, numero_orden (es el identificador del comprobante),
     * fecha_ingreso (la pone MySQL) ni fecha_entrega_real (la pone el trigger
     * trg_orden_before_update cuando la orden llega a un estado final).
     *
     * El estado tampoco se cambia por aqui: para eso esta cambiarEstado().
     */
    @Override
    protected void copiarDatos(OrdenServicio origen, OrdenServicio destino) {
        destino.setIdCliente(origen.getIdCliente());
        destino.setIdUsuario(origen.getIdUsuario());
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

    /**
     * BORRADO LOGICO: la orden pasa a "Cancelado", la fila se queda.
     *
     * Ojo: el trigger exige diagnostico y costo_final para cerrar una orden,
     * porque "Cancelado" es un estado final. Si faltan, MySQL lo rechaza y el
     * manejador global lo devuelve como 400 con el motivo. Es correcto: no se
     * cancela una orden sin dejar constancia de por que.
     */
    @Override
    @Transactional
    public void eliminar(Integer id) {
        OrdenServicio orden = buscarPorId(id);
        orden.setIdEstado(estadoCancelado().getIdEstado());
        ordenServicioRepository.save(orden);
    }

    @Override
    @Transactional
    public OrdenServicio cambiarEstado(Integer idOrden, Integer idEstado) {
        OrdenServicio orden = buscarPorId(idOrden);
        EstadoOrden estado = estadoOrdenRepository.findById(idEstado)
                .orElseThrow(() -> new RecursoNoEncontradoException("EstadoOrden", idEstado));
        orden.setIdEstado(estado.getIdEstado());
        return ordenServicioRepository.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenServicio> listarPorCliente(Integer idCliente) {
        return ordenServicioRepository.findByIdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenServicio> listarPorTecnico(Integer idUsuario) {
        return ordenServicioRepository.findByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenServicio> listarPorEstado(Integer idEstado) {
        return ordenServicioRepository.findByIdEstado(idEstado);
    }

    private EstadoOrden estadoCancelado() {
        return estadoOrdenRepository.findByNombreIgnoreCase(ESTADO_CANCELADO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "EstadoOrden con nombre", ESTADO_CANCELADO));
    }
}
