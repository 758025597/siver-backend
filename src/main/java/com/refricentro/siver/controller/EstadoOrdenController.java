package com.refricentro.siver.controller;

import com.refricentro.siver.dto.EstadoOrdenRequest;
import com.refricentro.siver.dto.EstadoOrdenResponse;
import com.refricentro.siver.modelos.EstadoOrden;
import com.refricentro.siver.service.EstadoOrdenService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints del catalogo de estados.
 *
 * Hereda los 5 del CRUD de ControladorGenerico. Es la UNICA entidad del
 * proyecto cuyo DELETE es FISICO: la fila desaparece de la tabla.
 * Aun asi, MySQL lo impide si alguna orden esta usando ese estado, porque
 * la llave foranea es ON DELETE RESTRICT: en ese caso responde 409.
 */
@RestController
@RequestMapping("/api/estados-orden")
public class EstadoOrdenController
        extends ControladorGenerico<EstadoOrden, Integer, EstadoOrdenRequest, EstadoOrdenResponse> {

    private final EstadoOrdenService estadoOrdenService;

    public EstadoOrdenController(EstadoOrdenService estadoOrdenService) {
        super(estadoOrdenService);
        this.estadoOrdenService = estadoOrdenService;
    }

    @Override
    protected EstadoOrden aEntidad(EstadoOrdenRequest request) {
        EstadoOrden estado = new EstadoOrden();
        estado.setNombre(request.nombre());
        estado.setDescripcion(request.descripcion());
        estado.setOrdenFlujo(request.ordenFlujo());
        estado.setEsFinal(request.esFinal());
        return estado;
    }

    @Override
    protected EstadoOrdenResponse aRespuesta(EstadoOrden estado) {
        return new EstadoOrdenResponse(
                estado.getIdEstado(),
                estado.getNombre(),
                estado.getDescripcion(),
                estado.getOrdenFlujo(),
                estado.getEsFinal());
    }

    /**
     * GET /api/estados-orden/flujo - los estados en el orden de atencion.
     * Sirve para dibujar la linea de tiempo de una orden en el frontend.
     */
    @GetMapping("/flujo")
    public ResponseEntity<List<EstadoOrdenResponse>> listarEnOrdenDeFlujo() {
        return ResponseEntity.ok(estadoOrdenService.listarEnOrdenDeFlujo().stream()
                .map(this::aRespuesta)
                .toList());
    }
}
