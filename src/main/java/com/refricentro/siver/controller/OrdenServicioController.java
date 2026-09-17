package com.refricentro.siver.controller;

import com.refricentro.siver.dto.OrdenServicioRequest;
import com.refricentro.siver.dto.OrdenServicioResponse;
import com.refricentro.siver.modelos.OrdenServicio;
import com.refricentro.siver.service.ClienteService;
import com.refricentro.siver.service.EstadoOrdenService;
import com.refricentro.siver.service.OrdenServicioService;
import com.refricentro.siver.service.UsuarioService;
import java.util.List;
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
public class OrdenServicioController
        extends ControladorGenerico<OrdenServicio, Integer,
                                    OrdenServicioRequest, OrdenServicioResponse> {

    private final OrdenServicioService ordenServicioService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final EstadoOrdenService estadoOrdenService;

    public OrdenServicioController(OrdenServicioService ordenServicioService,
                                   ClienteService clienteService,
                                   UsuarioService usuarioService,
                                   EstadoOrdenService estadoOrdenService) {
        super(ordenServicioService);
        this.ordenServicioService = ordenServicioService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.estadoOrdenService = estadoOrdenService;
    }

    @Override
    protected OrdenServicio aEntidad(OrdenServicioRequest request) {
        OrdenServicio orden = new OrdenServicio();

        // Se validan de verdad: si no existen, responde 404 y no 409.
        orden.setIdCliente(clienteService.buscarPorId(request.idCliente()).getIdCliente());
        orden.setIdUsuario(usuarioService.buscarPorId(request.idUsuario()).getIdUsuario());

        // Al crear, si no mandan estado se asume el primero del flujo.
        Integer idEstado = request.idEstado() != null
                ? estadoOrdenService.buscarPorId(request.idEstado()).getIdEstado()
                : estadoOrdenService.listarEnOrdenDeFlujo().getFirst().getIdEstado();
        orden.setIdEstado(idEstado);

        orden.setNumeroOrden(request.numeroOrden());
        orden.setEquipo(request.equipo());
        orden.setMarca(request.marca());
        orden.setModelo(request.modelo());
        orden.setNumeroSerie(request.numeroSerie());
        orden.setDescripcionFalla(request.descripcionFalla());
        orden.setDiagnostico(request.diagnostico());
        orden.setTrabajoRealizado(request.trabajoRealizado());
        orden.setFechaEntregaEstimada(request.fechaEntregaEstimada());
        orden.setCostoEstimado(request.costoEstimado());
        orden.setCostoFinal(request.costoFinal());
        return orden;
    }

    @Override
    protected OrdenServicioResponse aRespuesta(OrdenServicio o) {
        return new OrdenServicioResponse(
                o.getIdOrden(), o.getNumeroOrden(), o.getIdCliente(), o.getIdUsuario(),
                o.getIdEstado(), o.getEquipo(), o.getMarca(), o.getModelo(),
                o.getNumeroSerie(), o.getDescripcionFalla(), o.getDiagnostico(),
                o.getTrabajoRealizado(), o.getFechaIngreso(), o.getFechaEntregaEstimada(),
                o.getFechaEntregaReal(), o.getCostoEstimado(), o.getCostoFinal());
    }

    private ResponseEntity<List<OrdenServicioResponse>> responder(List<OrdenServicio> ordenes) {
        return ResponseEntity.ok(ordenes.stream().map(this::aRespuesta).toList());
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
        return responder(ordenServicioService.listarPorCliente(idCliente));
    }

    /** GET /api/ordenes-servicio/tecnico/{idUsuario} - carga de trabajo de un tecnico. */
    @GetMapping("/tecnico/{idUsuario}")
    public ResponseEntity<List<OrdenServicioResponse>> porTecnico(@PathVariable Integer idUsuario) {
        return responder(ordenServicioService.listarPorTecnico(idUsuario));
    }

    /** GET /api/ordenes-servicio/estado/{idEstado} - por ejemplo, las pendientes. */
    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<OrdenServicioResponse>> porEstado(@PathVariable Integer idEstado) {
        return responder(ordenServicioService.listarPorEstado(idEstado));
    }
}
