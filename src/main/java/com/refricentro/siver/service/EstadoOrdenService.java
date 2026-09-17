package com.refricentro.siver.service;

import com.refricentro.siver.modelos.EstadoOrden;
import java.util.List;

/**
 * Operaciones sobre el catalogo de estados.
 *
 * Es la UNICA entidad del proyecto con BORRADO FISICO: es un catalogo fijo
 * de 6 filas y la unica tabla sin columna `activo`. Aun asi, solo se podra
 * borrar un estado que ninguna orden este usando, porque la llave foranea
 * orden_servicio -> estado_orden es ON DELETE RESTRICT.
 */
public interface EstadoOrdenService extends CrudService<EstadoOrden, Integer> {

    /** Los estados ordenados por su posicion en el flujo. */
    List<EstadoOrden> listarEnOrdenDeFlujo();
}
