package com.refricentro.siver.service;

import com.refricentro.siver.modelos.OrdenServicio;
import java.util.List;

/**
 * Operaciones sobre las ordenes de servicio tecnico.
 *
 * El DELETE NO borra la fila: mueve la orden al estado "Cancelado".
 * Una orden es historial de atencion al cliente y no se puede perder.
 */
public interface OrdenServicioService extends CrudService<OrdenServicio, Integer> {

    /** Mueve la orden por el flujo: Recibido -> En revision -> ... */
    OrdenServicio cambiarEstado(Integer idOrden, Integer idEstado);

    List<OrdenServicio> listarPorCliente(Integer idCliente);

    List<OrdenServicio> listarPorTecnico(Integer idUsuario);

    List<OrdenServicio> listarPorEstado(Integer idEstado);
}
