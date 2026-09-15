package com.refricentro.siver.exception;

/**
 * Se lanza cuando se pide un registro que no existe.
 * El ManejadorGlobalErrores la convierte en un HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con id " + id + " no existe");
    }
}
