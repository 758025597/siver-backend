package com.refricentro.siver.controller;

import com.refricentro.siver.dto.NotificacionResponse;
import com.refricentro.siver.dto.PaginaResponse;
import com.refricentro.siver.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * La campanita del sistema. Cada usuario ve solo los avisos de su rol.
 *
 *   GET   /api/notificaciones?page=0&size=10   los avisos, con leida true/false
 *   GET   /api/notificaciones/no-leidas         { "cantidad": 3 }
 *   PATCH /api/notificaciones/{id}/leer
 *   PATCH /api/notificaciones/leer-todas
 */
@Tag(name = "Notificaciones", description = "Avisos dentro del sistema (stock bajo, ordenes vencidas...).")
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService servicio;

    @Operation(summary = "Mis notificaciones de los ultimos 30 dias, de la mas nueva a la mas antigua")
    @GetMapping
    public ResponseEntity<PaginaResponse<NotificacionResponse>> listar(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(PaginaResponse.de(servicio.listarMias(pageable), Function.identity()));
    }

    @Operation(summary = "Cuantas notificaciones no he leido")
    @GetMapping("/no-leidas")
    public ResponseEntity<Map<String, Long>> noLeidas() {
        return ResponseEntity.ok(Map.of("cantidad", servicio.contarNoLeidas()));
    }

    @Operation(summary = "Marca una notificacion como leida")
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> leer(@PathVariable Integer id) {
        servicio.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Marca todas mis notificaciones como leidas")
    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> leerTodas() {
        servicio.marcarTodasLeidas();
        return ResponseEntity.noContent().build();
    }
}
