package com.refricentro.siver.controller;

import com.refricentro.siver.dto.ClienteRequest;
import com.refricentro.siver.dto.ClienteResponse;
import com.refricentro.siver.model.Cliente;
import com.refricentro.siver.service.ClienteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController
        extends ControladorGenerico<Cliente, Integer, ClienteRequest, ClienteResponse> {

    private final ClienteService servicio;

    @Qualifier("defaultMapper")
    private final ModelMapper mapper;

    @Override
    protected ClienteService getServicio() {
        return servicio;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(Cliente cliente) {
        return cliente.getIdCliente();
    }

    /** GET /api/clientes/activos - solo los vigentes, para los combos del frontend. */
    @GetMapping("/activos")
    public ResponseEntity<List<ClienteResponse>> listarActivos() {
        return ResponseEntity.ok(aRespuestas(servicio.listarActivos()));
    }
}
