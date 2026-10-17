package com.refricentro.siver.service;

import com.refricentro.siver.model.Categoria;

import java.util.List;

public interface CategoriaService extends CrudService<Categoria, Integer> {

    List<Categoria> listarActivos();

}