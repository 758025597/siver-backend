package com.refricentro.siver.service;

import java.util.List;

/**
 * Contrato CRUD que cumplen todos los servicios del proyecto.
 *
 * @param <T>  tipo de la entidad (Producto, Usuario, ...)
 * @param <ID> tipo de su llave primaria (en este proyecto siempre Integer)
 */
public interface CrudService<T, ID> {

    /** C - crea un registro nuevo. */
    T crear(T entidad);

    /** R - devuelve todos los registros. */
    List<T> listar();

    /** R - devuelve uno por su id. Lanza RecursoNoEncontradoException si no existe. */
    T buscarPorId(ID id);

    /** U - actualiza los datos de un registro existente. */
    T actualizar(ID id, T datos);

    /**
     * D - elimina un registro.
     * Segun la entidad sera BORRADO FISICO o BORRADO LOGICO: eso lo decide
     * la clase de la que herede el servicio, no quien llama a este metodo.
     */
    void eliminar(ID id);
}
