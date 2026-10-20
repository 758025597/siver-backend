
package com.refricentro.siver.service.impl;

import com.refricentro.siver.model.Rol;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.repository.RolRepository;
import com.refricentro.siver.service.RolService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolServiceImpl extends ServicioSoftDelete<Rol, Integer>
        implements RolService {

    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    protected IGenericRepository<Rol, Integer> getRepo() {
        return rolRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "Rol";
    }

    @Override
    protected void copiarDatos(Rol origen, Rol destino) {
        destino.setNombre(origen.getNombre());
        destino.setDescripcion(origen.getDescripcion());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarActivos() {
        return rolRepository.findByActivoTrue();
    }
}
