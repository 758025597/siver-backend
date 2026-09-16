package com.refricentro.siver.service;

import com.refricentro.siver.modelos.Proveedor;

import java.util.List;

public interface ProveedorService extends CrudService<Proveedor, Integer> {

    List<Proveedor> listarActivos();
}