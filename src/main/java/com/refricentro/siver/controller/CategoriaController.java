package com.refricentro.siver.controller;

import com.refricentro.siver.dto.CategoriaRequest;
import com.refricentro.siver.dto.CategoriaResponse;
import com.refricentro.siver.modelos.Categoria;
import com.refricentro.siver.service.CategoriaService;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController
        extends ControladorGenerico<Categoria, Integer, CategoriaRequest, CategoriaResponse> {

    private final CategoriaService servicio;

    public CategoriaController(CategoriaService servicio) {
        super(servicio);
        this.servicio = servicio;
    }

    @Override
    protected Categoria aEntidad(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return categoria;
    }

    @Override
    protected CategoriaResponse aRespuesta(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getIdCat(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getActivo(),
                categoria.getFechaRegistro()
        );
    }

    /** GET /api/categorias/activos - solo los vigentes, para los combos del frontend. */
    @GetMapping("/activos")
    public ResponseEntity<List<CategoriaResponse>> listarActivos() {
        return ResponseEntity.ok(servicio.listarActivos().stream()
                .map(this::aRespuesta)
                .toList());
    }
}
