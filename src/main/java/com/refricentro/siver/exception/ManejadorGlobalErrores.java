package com.refricentro.siver.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Atrapa las excepciones de TODOS los controladores y las convierte en
 * respuestas JSON ordenadas.
 *
 * Sin esto, pedir un producto que no existe devuelve un stacktrace de 200
 * lineas con un 500, en vez de un 404 limpio.
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    /** 404 - se pidio un registro que no existe. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorRespuesta.de(404, "No encontrado", ex.getMessage()));
    }

    /** 400 - fallaron las validaciones del @Valid (@NotBlank, @Email, @Size...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));

        ErrorRespuesta cuerpo = new ErrorRespuesta(
                java.time.LocalDateTime.now(), 400, "Datos invalidos",
                "Revisa los campos marcados", campos);

        return ResponseEntity.badRequest().body(cuerpo);
    }

    /**
     * 400 - el valor de la URL no es del tipo esperado.
     *
     * Pasa al pedir /api/productos/abc: el id se espera Integer y llega
     * texto. Antes se iba al 500 generico. Afecta a los 11 modulos, porque
     * todos tienen endpoints con /{id}.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> tipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        String esperado = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "valor valido";
        String mensaje = "El parametro '" + ex.getName() + "' debe ser de tipo "
                + esperado + ", pero llego: " + ex.getValue();
        return ResponseEntity.badRequest()
                .body(ErrorRespuesta.de(400, "Parametro invalido", mensaje));
    }

    /**
     * 409 - choca con una restriccion de la base de datos.
     *
     * En este proyecto se dispara sobre todo por:
     *  - UNIQUE repetido (dni, correo, ruc, codigo de producto)
     *  - ON DELETE RESTRICT: intentar borrar algo que tiene hijos
     *  - los CHECK de las tablas (por ejemplo precio_venta >= precio_compra)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> integridad(DataIntegrityViolationException ex) {
        String detalle = ex.getMostSpecificCause().getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorRespuesta.de(409, "Conflicto con la base de datos", detalle));
    }

    /**
     * 400 - cualquier otro rechazo de la base que no sea de integridad.
     *
     * El caso tipico: mandar un valor que no esta en un ENUM de MySQL.
     * Este lanza "Data truncated for column", que NO es una
     * DataIntegrityViolationException y antes se iba al 500 generico.
     *
     * Va despues del handler de integridad a proposito: Spring elige
     * siempre el handler mas especifico que calce.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorRespuesta> baseDeDatos(DataAccessException ex) {
        String detalle = ex.getMostSpecificCause().getMessage();
        return ResponseEntity.badRequest()
                .body(ErrorRespuesta.de(400, "Dato rechazado por la base de datos", detalle));
    }

    /**
     * 405 - el endpoint existe pero no admite ese metodo HTTP.
     *
     * Es la respuesta correcta para las entidades que a proposito no tienen
     * las 4 operaciones: un POST o un DELETE sobre movimiento_inventario,
     * que es una bitacora de solo lectura.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> metodoNoPermitido(HttpRequestMethodNotSupportedException ex) {
        String permitidos = ex.getSupportedHttpMethods() == null ? "" :
                " Métodos permitidos: " + ex.getSupportedHttpMethods() + ".";
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorRespuesta.de(405, "Metodo no permitido",
                        "Este recurso no admite " + ex.getMethod() + "." + permitidos));
    }

    /** 500 - cualquier otra cosa no prevista. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> general(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorRespuesta.de(500, "Error interno", ex.getMessage()));
    }
}
