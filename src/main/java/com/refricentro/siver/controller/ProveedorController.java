package com.refricentro.siver.controller;

import com.refricentro.siver.dto.ProveedorRequest;
import com.refricentro.siver.dto.ProveedorResponse;
import com.refricentro.siver.model.Proveedor;
import com.refricentro.siver.service.ProveedorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController
        extends ControladorGenerico<Proveedor, Integer, ProveedorRequest, ProveedorResponse> {

    private final ProveedorService servicio;

    @Qualifier("defaultMapper")
    private final ModelMapper mapper;

    @Override
    protected ProveedorService getServicio() {
        return servicio;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(Proveedor proveedor) {
        return proveedor.getIdProveedor();
    }

    /** GET /api/proveedores/activos - solo los vigentes, para los combos del frontend. */
    @GetMapping("/activos")
    public ResponseEntity<List<ProveedorResponse>> listarActivos() {
        return ResponseEntity.ok(aRespuestas(servicio.listarActivos()));
    }
}
