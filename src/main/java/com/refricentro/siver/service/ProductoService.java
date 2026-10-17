package com.refricentro.siver.service;

import com.refricentro.siver.model.Producto;

import java.util.List;

public interface ProductoService extends CrudService<Producto, Integer> {

    List<Producto> listarActivos();

    List<Producto> buscarConStockBajo();
}