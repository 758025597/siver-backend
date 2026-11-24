package com.refricentro.siver.controller;

import com.refricentro.siver.dto.DashboardResponse;
import com.refricentro.siver.security.UsuarioActual;
import com.refricentro.siver.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/dashboard - indicadores del panel principal.
 *
 * El rol NO llega como parametro (se podria falsificar): se toma del JWT.
 */
@Tag(name = "Dashboard", description = "Indicadores (KPI) del panel principal, segun el rol.")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService servicio;
    private final UsuarioActual usuarioActual;

    @Operation(summary = "Indicadores del dia y del mes adaptados al rol del usuario")
    @GetMapping
    public ResponseEntity<DashboardResponse> resumen() {
        return ResponseEntity.ok(servicio.resumen(usuarioActual.rol().orElse("SIN_ROL")));
    }
}
