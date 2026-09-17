package com.refricentro.siver.service;

import com.refricentro.siver.modelos.MovimientoInventario;
import java.util.List;

/**
 * Consultas sobre la bitacora de inventario.
 *
 * OJO: esta interfaz NO extiende CrudService, y es a proposito.
 *
 * movimiento_inventario es una BITACORA DE AUDITORIA: registra cada entrada
 * y salida de stock con su motivo. Las filas las escriben los triggers de
 * MySQL (trg_detalle_venta_after_insert), no la API.
 *
 * De las 4 operaciones del CRUD solo tiene sentido la R:
 *   C - no: un movimiento inventado a mano es un registro falso
 *   R - si: es justamente para lo que existe
 *   U - no: reescribir el historial lo vuelve inutil como auditoria
 *   D - no: y ademas seria borrado FISICO, se perderia para siempre
 *
 * Una bitacora que cualquiera puede escribir y borrar no sirve como bitacora:
 * es el unico rastro de por que cambio el stock de un producto.
 */
public interface MovimientoInventarioService {

    List<MovimientoInventario> listar();

    MovimientoInventario buscarPorId(Integer id);

    List<MovimientoInventario> listarPorProducto(Integer idProducto);

    List<MovimientoInventario> listarPorUsuario(Integer idUsuario);

    List<MovimientoInventario> listarPorVenta(Integer idVenta);
}
