package com.refricentro.siver.controller;

import com.refricentro.siver.dto.RolRequest;
import com.refricentro.siver.dto.RolResponse;
import com.refricentro.siver.modelos.Rol;
import com.refricentro.siver.service.RolService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de rol.
 *
 * Los 5 del CRUD los hereda de ControladorGenerico:
 *   GET    /api/roles
 *   GET    /api/roles/{id}
 *   POST   /api/roles
 *   PUT    /api/roles/{id}
 *   DELETE /api/roles/{id}   -> borrado logico, deja activo = false
 *
 * Aqui solo se escriben las dos conversiones y los endpoints extra.
 */
@RestController
@RequestMapping("/api/roles")
public class RolController
        extends ControladorGenerico<Rol, Integer, RolRequest, RolResponse> {

    private final RolService rolService;

    public RolController(RolService rolService) {
        super(rolService);
        this.rolService = rolService;
    }

    @Override
    protected Rol aEntidad(RolRequest request) {
        Rol rol = new Rol();
        rol.setNombre(request.nombre());
        rol.setDescripcion(request.descripcion());
        return rol;
    }

    @Override
    protected RolResponse aRespuesta(Rol rol) {
        return new RolResponse(
                rol.getIdRol(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getActivo(),
                rol.getFechaRegistro());
    }

    /** GET /api/roles/activos - solo los vigentes, para llenar los combos del frontend. */
    @GetMapping("/activos")
    public ResponseEntity<List<RolResponse>> listarActivos() {
        List<RolResponse> respuesta = rolService.listarActivos().stream()
                .map(this::aRespuesta)
                .toList();
        return ResponseEntity.ok(respuesta);
    }
}
