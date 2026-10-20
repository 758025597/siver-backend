package com.refricentro.siver.controller;

import com.refricentro.siver.dto.RolRequest;
import com.refricentro.siver.dto.RolResponse;
import com.refricentro.siver.model.Rol;
import com.refricentro.siver.service.RolService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * Aqui solo se elige el mapper y se escriben los endpoints extra.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController
        extends ControladorGenerico<Rol, Integer, RolRequest, RolResponse> {

    private final RolService rolService;

    @Qualifier("rolMapper")
    private final ModelMapper mapper;

    @Override
    protected RolService getServicio() {
        return rolService;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(Rol rol) {
        return rol.getIdRol();
    }

    /** GET /api/roles/activos - solo los vigentes, para llenar los combos del frontend. */
    @GetMapping("/activos")
    public ResponseEntity<List<RolResponse>> listarActivos() {
        return ResponseEntity.ok(aRespuestas(rolService.listarActivos()));
    }
}
