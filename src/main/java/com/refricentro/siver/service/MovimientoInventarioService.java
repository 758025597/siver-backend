package com.refricentro.siver.service;

import com.refricentro.siver.modelos.MovimientoInventario;

import java.util.List;

public interface MovimientoInventarioService
        extends CrudService<MovimientoInventario, Integer> {

    List<MovimientoInventario> listarPorProducto(Integer idProducto);

    List<MovimientoInventario> listarPorUsuario(Integer idUsuario);

    List<MovimientoInventario> listarPorVenta(Integer idVenta);
}