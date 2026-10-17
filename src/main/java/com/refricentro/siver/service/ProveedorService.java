package com.refricentro.siver.service;

import com.refricentro.siver.model.Proveedor;

import java.util.List;

public interface ProveedorService extends CrudService<Proveedor, Integer> {

    List<Proveedor> listarActivos();
}