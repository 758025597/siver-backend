package com.refricentro.siver.controller;

import com.refricentro.siver.dto.EstadoOrdenRequest;
import com.refricentro.siver.dto.EstadoOrdenResponse;
import com.refricentro.siver.model.EstadoOrden;
import com.refricentro.siver.service.EstadoOrdenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RequiredArgsConstructor
public class EstadoOrdenController
        extends ControladorGenerico<EstadoOrden, Integer, EstadoOrdenRequest, EstadoOrdenResponse> {

    private final EstadoOrdenService estadoOrdenService;

    @Qualifier("estadoOrdenMapper")
    private final ModelMapper mapper;

    @Override
    protected EstadoOrdenService getServicio() {
        return estadoOrdenService;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(EstadoOrden estado) {
        return estado.getIdEstado();
    }

    /**
     * GET /api/estados-orden/flujo - los estados en el orden de atencion.
     * Sirve para dibujar la linea de tiempo de una orden en el frontend.
     */
    @GetMapping("/flujo")
    public ResponseEntity<List<EstadoOrdenResponse>> listarEnOrdenDeFlujo() {
        return ResponseEntity.ok(aRespuestas(estadoOrdenService.listarEnOrdenDeFlujo()));
    }
}
