package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.Rol;
import com.refricentro.siver.repository.RolRepository;
import com.refricentro.siver.service.RolService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio de rol.
 *
 * Hereda de ServicioSoftDelete, asi que su eliminar() pone activo = false
 * en vez de borrar la fila. Es lo correcto aqui porque la llave foranea
 * usuario -> rol es ON DELETE RESTRICT: MySQL impide borrar un rol que
 * tenga usuarios asignados.
 */
@Service
public class RolServiceImpl extends ServicioSoftDelete<Rol, Integer> implements RolService {

    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        super(rolRepository);
        this.rolRepository = rolRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "Rol";
    }

    /**
     * Campos que SI se pueden modificar con un PUT.
     * No se copian el id, ni activo (eso lo maneja eliminar), ni
     * fecha_registro (la pone MySQL y es de solo lectura).
     */
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
