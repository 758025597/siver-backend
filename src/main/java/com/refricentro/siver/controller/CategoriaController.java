package com.refricentro.siver.controller;

import com.refricentro.siver.dto.CategoriaRequest;
import com.refricentro.siver.dto.CategoriaResponse;
import com.refricentro.siver.model.Categoria;
import com.refricentro.siver.service.CategoriaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController
        extends ControladorGenerico<Categoria, Integer, CategoriaRequest, CategoriaResponse> {

    private final CategoriaService servicio;

    @Qualifier("defaultMapper")
    private final ModelMapper mapper;

    @Override
    protected CategoriaService getServicio() {
        return servicio;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(Categoria categoria) {
        return categoria.getIdCat();
    }

    /** GET /api/categorias/activos - solo los vigentes, para los combos del frontend. */
    @GetMapping("/activos")
    public ResponseEntity<List<CategoriaResponse>> listarActivos() {
        return ResponseEntity.ok(aRespuestas(servicio.listarActivos()));
    }
}
