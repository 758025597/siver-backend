
package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.service.CrudService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public abstract class ServicioGenerico<T, ID> implements CrudService<T, ID> {

    protected abstract IGenericRepository<T, ID> getRepo();

    protected abstract String nombreRecurso();

    protected abstract void copiarDatos(T origen, T destino);

    /**
     * La busqueda por texto de cada entidad. Cada servicio la implementa
     * llamando a su consulta JPQL (ver el @Query de su repositorio).
     */
    protected abstract Page<T> buscar(String texto, Pageable pageable);

    @Override
    @Transactional(readOnly = true)
    public Page<T> listarPaginado(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return getRepo().findAll(pageable);
        }
        return buscar(filtro.trim(), pageable);
    }

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
