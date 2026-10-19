package com.refricentro.siver.controller;

import com.refricentro.siver.dto.OrdenServicioRequest;
import com.refricentro.siver.dto.OrdenServicioResponse;
import com.refricentro.siver.model.OrdenServicio;
import com.refricentro.siver.service.ClienteService;
import com.refricentro.siver.service.EstadoOrdenService;
import com.refricentro.siver.service.OrdenServicioService;
import com.refricentro.siver.service.UsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de las ordenes de servicio tecnico.
 *
 * Hereda los 5 del CRUD de ControladorGenerico, pero el DELETE NO borra:
 * el servicio lo sobrescribe para mover la orden al estado "Cancelado".
 * Una orden es historial de atencion al cliente y no se puede perder.
 *
 * Inyecta ClienteService, UsuarioService y EstadoOrdenService solo para
 * validar que los ids que llegan existan, y asi responder 404 con un
 * mensaje claro en vez de dejar que reviente la llave foranea.
 */
@RestController
@RequestMapping("/api/ordenes-servicio")
@RequiredArgsConstructor
public class OrdenServicioController
        extends ControladorGenerico<OrdenServicio, Integer,
        OrdenServicioRequest, OrdenServicioResponse> {

    private final OrdenServicioService ordenServicioService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final EstadoOrdenService estadoOrdenService;

    @Qualifier("ordenServicioMapper")
    private final ModelMapper mapper;

    @Override
    protected OrdenServicioService getServicio() {
        return ordenServicioService;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(OrdenServicio orden) {
        return orden.getIdOrden();
    }

    /** ModelMapper copia los datos del equipo; cliente, tecnico y estado se buscan aparte. */
    @Override
    protected OrdenServicio aEntidad(OrdenServicioRequest request) {
        OrdenServicio orden = super.aEntidad(request);
        // Se validan de verdad: si no existen, responde 404 y no 409.
        orden.setCliente(clienteService.buscarPorId(request.getIdCliente()));
        orden.setTecnico(usuarioService.buscarPorId(request.getIdUsuario()));
        // Al crear, si no mandan estado se asume el primero del flujo.
        orden.setEstado(request.getIdEstado() != null
                ? estadoOrdenService.buscarPorId(request.getIdEstado())
                : estadoOrdenService.listarEnOrdenDeFlujo().getFirst());
        return orden;
    }

    /**
     * PATCH /api/ordenes-servicio/{id}/estado/{idEstado} - mueve la orden por el flujo.
     *
     * Al pasar a un estado final, el trigger trg_orden_before_update exige
     * diagnostico y costo_final, y pone la fecha de entrega real solo.
     */
    @PatchMapping("/{id}/estado/{idEstado}")
    public ResponseEntity<OrdenServicioResponse> cambiarEstado(@PathVariable Integer id,
                                                               @PathVariable Integer idEstado) {
        return ResponseEntity.ok(aRespuesta(ordenServicioService.cambiarEstado(id, idEstado)));
    }

    /** GET /api/ordenes-servicio/cliente/{idCliente} - historial de un cliente. */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<OrdenServicioResponse>> porCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(aRespuestas(ordenServicioService.listarPorCliente(idCliente)));
    }

    /** GET /api/ordenes-servicio/tecnico/{idUsuario} - carga de trabajo de un tecnico. */
    @GetMapping("/tecnico/{idUsuario}")
    public ResponseEntity<List<OrdenServicioResponse>> porTecnico(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(aRespuestas(ordenServicioService.listarPorTecnico(idUsuario)));
    }

    /** GET /api/ordenes-servicio/estado/{idEstado} - por ejemplo, las pendientes. */
    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<OrdenServicioResponse>> porEstado(@PathVariable Integer idEstado) {
        return ResponseEntity.ok(aRespuestas(ordenServicioService.listarPorEstado(idEstado)));
    }
}
