
package com.refricentro.siver.service.impl;

import com.refricentro.siver.model.EstadoOrden;
import com.refricentro.siver.repository.EstadoOrdenRepository;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.service.EstadoOrdenService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstadoOrdenServiceImpl
        extends ServicioGenerico<EstadoOrden, Integer>
        implements EstadoOrdenService {

    private final EstadoOrdenRepository estadoOrdenRepository;

    public EstadoOrdenServiceImpl(EstadoOrdenRepository estadoOrdenRepository) {
        this.estadoOrdenRepository = estadoOrdenRepository;
    }

    @Override
    protected IGenericRepository<EstadoOrden, Integer> getRepo() {
        return estadoOrdenRepository;
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

    /** Busqueda paginada con la consulta JPQL del repositorio. */
    @Override
    protected Page<EstadoOrden> buscar(String texto, Pageable pageable) {
        return estadoOrdenRepository.buscar(texto, pageable);
    }
}
