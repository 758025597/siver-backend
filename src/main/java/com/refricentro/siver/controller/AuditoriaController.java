package com.refricentro.siver.controller;

import com.refricentro.siver.dto.AuditoriaResponse;
import com.refricentro.siver.dto.PaginaResponse;
import com.refricentro.siver.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta de la auditoria. Solo el ADMINISTRADOR (regla en SecurityConfig).
 * Es de solo lectura: la bitacora no se edita ni se borra.
 */
@Tag(name = "Auditoria", description = "Quien hizo que y cuando. Solo administrador.")
@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService servicio;

    @Operation(summary = "Bitacora paginada con filtros",
            description = "buscar: correo o ruta. accion: CREAR, ACTUALIZAR, ELIMINAR, ANULAR, CAMBIAR_ESTADO, LOGIN, LOGIN_FALLIDO.")
    @GetMapping("/paginado")
    public ResponseEntity<PaginaResponse<AuditoriaResponse>> listar(
            @Parameter(description = "Correo del usuario o parte de la ruta") @RequestParam(required = false) String buscar,
            @Parameter(description = "clientes, ventas, productos...") @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @ParameterObject @PageableDefault(sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(PaginaResponse.de(
                servicio.buscar(buscar, modulo, accion,
                        desde == null ? null : desde.atStartOfDay(),
                        hasta == null ? null : hasta.atTime(LocalTime.MAX),
                        pageable),
                AuditoriaResponse::de));
    }
}
