package com.refricentro.siver.controller;

import com.refricentro.siver.dto.DetalleVentaRequest;
import com.refricentro.siver.dto.DetalleVentaResponse;
import com.refricentro.siver.dto.VentaRequest;
import com.refricentro.siver.dto.VentaResponse;
import com.refricentro.siver.modelos.DetalleVenta;
import com.refricentro.siver.modelos.Venta;
import com.refricentro.siver.service.ClienteService;
import com.refricentro.siver.service.ProductoService;
import com.refricentro.siver.service.UsuarioService;
import com.refricentro.siver.service.VentaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de ventas.
 *
 * NO hereda de ControladorGenerico, y es a proposito: esa clase trae PUT y
 * DELETE, y una venta no se edita ni se borra.
 *
 *   GET    /api/ventas                 lista
 *   GET    /api/ventas/{id}            una venta con sus lineas
 *   POST   /api/ventas                 registra la venta completa
 *   PATCH  /api/ventas/{id}/anular     anula el comprobante
 *
 * Un PUT o un DELETE sobre /api/ventas/{id} responden 405, que es la
 * respuesta correcta: esas operaciones no existen para un comprobante.
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public VentaController(VentaService ventaService,
                           ClienteService clienteService,
                           UsuarioService usuarioService,
                           ProductoService productoService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    /** GET /api/ventas */
    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(ventaService.listar().stream()
                .map(this::aRespuesta)
                .toList());
    }

    /** GET /api/ventas/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(aRespuesta(ventaService.buscarPorId(id)));
    }

    /**
     * POST /api/ventas - registra la venta con todas sus lineas.
     *
     * Puede responder 400 con el motivo que dio MySQL cuando un trigger la
     * rechaza: "Stock insuficiente para completar la venta", "No se puede
     * vender un producto inactivo" o "Solo se puede emitir factura a
     * clientes con RUC".
     */
    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request) {
        Venta venta = new Venta();
        venta.setNumeroComprobante(request.numeroComprobante());
        venta.setTipoComprobante(request.tipoComprobante());
        venta.setMetodoPago(request.metodoPago());
        venta.setObservacion(request.observacion());

        // Se validan de verdad: si no existen, responde 404 y no 409.
        venta.setCliente(clienteService.buscarPorId(request.idCliente()));
        venta.setUsuario(usuarioService.buscarPorId(request.idUsuario()));

        List<DetalleVenta> detalles = request.detalles().stream()
                .map(this::aDetalle)
                .toList();

        Venta registrada = ventaService.registrar(venta, detalles);
        return ResponseEntity.status(HttpStatus.CREATED).body(aRespuesta(registrada));
    }

    /**
     * PATCH /api/ventas/{id}/anular
     *
     * Este es el "borrado" de una venta. La fila no se toca: pasa a ANULADA.
     * Un comprobante tributario no se elimina, se anula.
     */
    @PatchMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anular(@PathVariable Integer id) {
        return ResponseEntity.ok(aRespuesta(ventaService.anular(id)));
    }

    /** GET /api/ventas/cliente/{idCliente} - historial de compras de un cliente. */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaResponse>> porCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(ventaService.listarPorCliente(idCliente).stream()
                .map(this::aRespuesta)
                .toList());
    }

    /** GET /api/ventas/vendedor/{idUsuario} - ventas de un vendedor. */
    @GetMapping("/vendedor/{idUsuario}")
    public ResponseEntity<List<VentaResponse>> porVendedor(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(ventaService.listarPorVendedor(idUsuario).stream()
                .map(this::aRespuesta)
                .toList());
    }

    private DetalleVenta aDetalle(DetalleVentaRequest r) {
        DetalleVenta d = new DetalleVenta();
        d.setProducto(productoService.buscarPorId(r.idProducto()));
        d.setCantidad(r.cantidad());
        d.setPrecioUnitario(r.precioUnitario());
        d.setDescuento(r.descuentoOCero());
        d.setSubtotal(java.math.BigDecimal.ZERO); // lo calcula el trigger
        return d;
    }

    private VentaResponse aRespuesta(Venta v) {
        List<DetalleVentaResponse> lineas = ventaService.listarDetalles(v.getIdVenta()).stream()
                .map(d -> new DetalleVentaResponse(
                        d.getIdDetalle(),
                        d.getProducto() != null ? d.getProducto().getIdProducto() : null,
                        d.getProducto() != null ? d.getProducto().getNombre() : null,
                        d.getCantidad(),
                        d.getPrecioUnitario(), d.getDescuento(), d.getSubtotal()))
                .toList();

        return new VentaResponse(
                v.getIdVenta(), v.getNumeroComprobante(), v.getTipoComprobante(),
                v.getCliente() != null ? v.getCliente().getIdCliente() : null,
                v.getCliente() != null ? v.getCliente().getNombres() : null,
                v.getUsuario() != null ? v.getUsuario().getIdUsuario() : null,
                v.getFechaVenta(), v.getMetodoPago(),
                v.getSubtotal(), v.getIgv(), v.getTotal(), v.getEstado(),
                v.getObservacion(), lineas);
    }
}
