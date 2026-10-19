package com.refricentro.siver.controller;

import com.refricentro.siver.dto.DetalleVentaRequest;
import com.refricentro.siver.dto.DetalleVentaResponse;
import com.refricentro.siver.dto.VentaRequest;
import com.refricentro.siver.dto.VentaResponse;
import com.refricentro.siver.model.DetalleVenta;
import com.refricentro.siver.model.Venta;
import com.refricentro.siver.service.ClienteService;
import com.refricentro.siver.service.ProductoService;
import com.refricentro.siver.service.UsuarioService;
import com.refricentro.siver.service.VentaService;
import com.refricentro.siver.util.MapperUtil;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    @Qualifier("ventaMapper")
    private final ModelMapper mapper;

    /** GET /api/ventas */
    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(aRespuestas(ventaService.listar()));
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
        // ModelMapper copia comprobante, tipo, metodo de pago y observacion.
        Venta venta = mapper.map(request, Venta.class);

        // Se validan de verdad: si no existen, responde 404 y no 409.
        venta.setCliente(clienteService.buscarPorId(request.getIdCliente()));
        venta.setUsuario(usuarioService.buscarPorId(request.getIdUsuario()));

        List<DetalleVenta> detalles = request.getDetalles().stream()
                .map(this::aDetalle)
                .toList();

        Venta registrada = ventaService.registrar(venta, detalles);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registrada.getIdVenta())
                .toUri();
        return ResponseEntity.created(ubicacion).body(aRespuesta(registrada));
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
        return ResponseEntity.ok(aRespuestas(ventaService.listarPorCliente(idCliente)));
    }

    /** GET /api/ventas/vendedor/{idUsuario} - ventas de un vendedor. */
    @GetMapping("/vendedor/{idUsuario}")
    public ResponseEntity<List<VentaResponse>> porVendedor(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(aRespuestas(ventaService.listarPorVendedor(idUsuario)));
    }

    private DetalleVenta aDetalle(DetalleVentaRequest r) {
        // ModelMapper copia cantidad, precio y descuento; el producto se busca aparte.
        DetalleVenta d = mapper.map(r, DetalleVenta.class);
        d.setProducto(productoService.buscarPorId(r.getIdProducto()));
        if (d.getDescuento() == null) {
            d.setDescuento(BigDecimal.ZERO);
        }
        d.setSubtotal(BigDecimal.ZERO); // lo calcula el trigger
        return d;
    }

    private VentaResponse aRespuesta(Venta v) {
        VentaResponse respuesta = mapper.map(v, VentaResponse.class);
        respuesta.setDetalles(MapperUtil.mapList(
                mapper, ventaService.listarDetalles(v.getIdVenta()), DetalleVentaResponse.class));
        return respuesta;
    }

    private List<VentaResponse> aRespuestas(List<Venta> ventas) {
        return ventas.stream().map(this::aRespuesta).toList();
    }
}
