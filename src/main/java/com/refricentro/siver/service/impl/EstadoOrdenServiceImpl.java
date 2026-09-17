package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.EstadoOrden;
import com.refricentro.siver.repository.EstadoOrdenRepository;
import com.refricentro.siver.service.EstadoOrdenService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica del catalogo de estados.
 *
 * Hereda de ServicioGenerico y NO de ServicioSoftDelete: su eliminar() es
 * BORRADO FISICO. Es el unico caso del proyecto, y esta justificado porque
 * estado_orden es un catalogo fijo y es la unica tabla sin columna `activo`.
 *
 * Igual esta protegido: si una orden usa ese estado, la llave foranea
 * ON DELETE RESTRICT hace que MySQL rechace el borrado, y el manejador
 * global lo devuelve como 409.
 */
@Service
public class EstadoOrdenServiceImpl extends ServicioGenerico<EstadoOrden, Integer>
        implements EstadoOrdenService {

    private final EstadoOrdenRepository estadoOrdenRepository;

    public EstadoOrdenServiceImpl(EstadoOrdenRepository estadoOrdenRepository) {
        super(estadoOrdenRepository);
        this.estadoOrdenRepository = estadoOrdenRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "EstadoOrden";
    }

    @Override
    protected void copiarDatos(EstadoOrden origen, EstadoOrden destino) {
        destino.setNombre(origen.getNombre());
        destino.setDescripcion(origen.getDescripcion());
        destino.setOrdenFlujo(origen.getOrdenFlujo());
        destino.setEsFinal(origen.getEsFinal());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoOrden> listarEnOrdenDeFlujo() {
        return estadoOrdenRepository.findAllByOrderByOrdenFlujoAsc();
    }
}
