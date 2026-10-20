
package com.refricentro.siver.service.impl;

import com.refricentro.siver.model.Activable;
import com.refricentro.siver.repository.IGenericRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class ServicioSoftDelete<T extends Activable, ID>
        extends ServicioGenerico<T, ID> {

    @Override
    @Transactional
    public void eliminar(ID id) {
        T entidad = buscarPorId(id);
        entidad.setActivo(false);
        getRepo().save(entidad);
    }
}
