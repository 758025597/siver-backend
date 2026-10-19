
package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.service.CrudService;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public abstract class ServicioGenerico<T, ID> implements CrudService<T, ID> {

    protected abstract IGenericRepository<T, ID> getRepo();

    protected abstract String nombreRecurso();

    protected abstract void copiarDatos(T origen, T destino);

    @Override
    @Transactional
    public T crear(T entidad) {
        return getRepo().save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> listar() {
        return getRepo().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public T buscarPorId(ID id) {
        return getRepo().findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(nombreRecurso(), id));
    }

    @Override
    @Transactional
    public T actualizar(ID id, T datos) {
        T existente = buscarPorId(id);
        copiarDatos(datos, existente);
        return getRepo().save(existente);
    }

    @Override
    @Transactional
    public void eliminar(ID id) {
        getRepo().delete(buscarPorId(id));
    }
}
