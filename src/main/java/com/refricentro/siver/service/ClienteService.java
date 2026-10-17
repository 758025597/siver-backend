package com.refricentro.siver.service;

import com.refricentro.siver.model.Cliente;

import java.util.List;

public interface ClienteService extends CrudService<Cliente, Integer> {

    List<Cliente> listarActivos();
}