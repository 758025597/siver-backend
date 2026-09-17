package com.refricentro.siver.service;

import com.refricentro.siver.modelos.Rol;
import java.util.List;

/**
 * Operaciones disponibles sobre rol.
 *
 * Los 5 metodos del CRUD los hereda de CrudService, asi que aqui solo
 * se declara lo que es propio de esta entidad.
 */
public interface RolService extends CrudService<Rol, Integer> {

    /** Listado sin los roles desactivados. */
    List<Rol> listarActivos();
}
