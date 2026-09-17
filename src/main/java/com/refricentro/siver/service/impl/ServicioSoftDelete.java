package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.Activable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Igual que ServicioGenerico, pero con BORRADO LOGICO (soft delete).
 *
 * Lo unico que cambia es el metodo eliminar(): en vez de borrar la fila,
 * le pone activo = false. Todo lo demas se hereda sin escribir nada.
 *
 * De aqui heredan los servicios de las 6 entidades con columna `activo`:
 * Rol, Usuario, Cliente, Categoria, Proveedor y Producto.
 *
 * Por que soft delete y no fisico: 10 de las 13 llaves foraneas de siver_bd
 * son ON DELETE RESTRICT. MySQL IMPIDE borrar un registro que tenga hijos,
 * asi que el borrado fisico fallaria. Ademas se perderia el historial: un
 * usuario que firmo ventas no se puede borrar sin romper la trazabilidad.
 */
public abstract class ServicioSoftDelete<T extends Activable, ID>
        extends ServicioGenerico<T, ID> {

    protected ServicioSoftDelete(JpaRepository<T, ID> repositorio) {
        super(repositorio);
    }

    /**
     * BORRADO LOGICO: el registro se queda en la tabla con activo = false.
     *
     * Este es el unico metodo que se sobrescribe. Quien llama al servicio
     * hace servicio.eliminar(id) sin enterarse de si el borrado fue fisico
     * o logico: eso lo decide la herencia. Eso es polimorfismo.
     */
    @Override
    @Transactional
    public void eliminar(ID id) {
        T entidad = buscarPorId(id);
        entidad.setActivo(false);
        repositorio.save(entidad);
    }
}
