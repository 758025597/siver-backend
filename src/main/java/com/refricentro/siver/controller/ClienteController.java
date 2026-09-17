package com.refricentro.siver.controller;

import com.refricentro.siver.dto.ClienteRequest;
import com.refricentro.siver.dto.ClienteResponse;
import com.refricentro.siver.modelos.Cliente;
import com.refricentro.siver.service.ClienteService;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController
        extends ControladorGenerico<Cliente, Integer, ClienteRequest, ClienteResponse> {

    private final ClienteService servicio;

    public ClienteController(ClienteService servicio) {
        super(servicio);
        this.servicio = servicio;
    }

    @Override
    protected Cliente aEntidad(ClienteRequest request) {
        Cliente cliente = new Cliente();

        cliente.setTipoDocumento(request.getTipoDocumento());
        cliente.setNumeroDocumento(request.getNumeroDocumento());
        cliente.setNombres(request.getNombres());
        cliente.setTelefono(request.getTelefono());
        cliente.setCorreo(request.getCorreo());
        cliente.setDireccion(request.getDireccion());

        return cliente;
    }

    @Override
    protected ClienteResponse aRespuesta(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombres(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getDireccion(),
                cliente.getActivo(),
                cliente.getFechaRegistro()
        );
    }

    /** GET /api/clientes/activos - solo los vigentes, para los combos del frontend. */
    @GetMapping("/activos")
    public ResponseEntity<List<ClienteResponse>> listarActivos() {
        return ResponseEntity.ok(servicio.listarActivos().stream()
                .map(this::aRespuesta)
                .toList());
    }
}
