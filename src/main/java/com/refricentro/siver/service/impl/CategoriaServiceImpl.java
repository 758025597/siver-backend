package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.Categoria;
import com.refricentro.siver.repository.CategoriaRepository;
import com.refricentro.siver.service.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl
        extends ServicioSoftDelete<Categoria, Integer>
        implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        super(categoriaRepository);
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "Categoria";
    }

    @Override
    protected void copiarDatos(Categoria origen, Categoria destino) {
        destino.setNombre(origen.getNombre());
        destino.setDescripcion(origen.getDescripcion());
    }

    @Override
    public List<Categoria> listarActivos() {
        return categoriaRepository.findByActivoTrue();
    }
}