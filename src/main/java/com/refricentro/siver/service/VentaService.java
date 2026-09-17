package com.refricentro.siver.service;

import com.refricentro.siver.modelos.DetalleVenta;
import com.refricentro.siver.modelos.Venta;
import java.util.List;

/**
 * Operaciones sobre las ventas.
 *
 * OJO: esta interfaz NO extiende CrudService, y es a proposito.
 *
 * Una venta es un COMPROBANTE TRIBUTARIO (boleta o factura). De las 4
 * operaciones del CRUD, dos no aplican:
 *
 *   C - si, pero la venta se registra COMPLETA: cabecera y lineas juntas,
 *       en una sola transaccion. Una venta sin productos no existe.
 *   R - si.
 *   U - NO. Una boleta emitida no se corrige: se anula y se emite otra.
 *       Ademas los montos los calculan los triggers, no la aplicacion.
 *   D - NO. Borrar un comprobante seria borrar un documento tributario.
 *       En su lugar se ANULA, que deja el registro con estado = ANULADA.
 *
 * Por eso aqui hay registrar() y anular() en vez de crear(), actualizar()
 * y eliminar().
 */
public interface VentaService {

    /**
     * Registra una venta completa: cabecera mas sus lineas.
     *
     * Los triggers de MySQL hacen el resto: validan que haya stock, calculan
     * el subtotal de cada linea, descuentan el inventario, escriben la
     * bitacora de movimientos y recalculan los totales de la cabecera.
     */
    Venta registrar(Venta venta, List<DetalleVenta> detalles);

    List<Venta> listar();

    Venta buscarPorId(Integer id);

    /** Las lineas de una venta. */
    List<DetalleVenta> listarDetalles(Integer idVenta);

    /** Anula el comprobante: no borra nada, deja estado = ANULADA. */
    Venta anular(Integer id);

    List<Venta> listarPorCliente(Integer idCliente);

    List<Venta> listarPorVendedor(Integer idUsuario);

    List<Venta> listarPorEstado(Venta.EstadoVenta estado);
}
