package com.refricentro.siver.controller;

import com.refricentro.siver.dto.DetalleVentaRequest;
import com.refricentro.siver.dto.DetalleVentaResponse;
import com.refricentro.siver.dto.PaginaResponse;
import com.refricentro.siver.dto.VentaRequest;
import com.refricentro.siver.dto.VentaResponse;
import com.refricentro.siver.exception.ErrorRespuesta;
import com.refricentro.siver.model.DetalleVenta;
import com.refricentro.siver.model.Venta;
import com.refricentro.siver.service.ClienteService;
import com.refricentro.siver.service.ProductoService;
import com.refricentro.siver.service.UsuarioService;
import com.refricentro.siver.service.VentaService;
import com.refricentro.siver.util.MapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Endpoints de ventas.
 *
 * NO hereda de ControladorGenerico, y es a proposito: esa clase trae PUT y
 * DELETE, y una venta no se edita ni se borra.
 *
 *   GET    /api/ventas                 lista
 *   GET    /api/ventas/paginado        una pagina (?page=0&size=10&buscar=)
 *   GET    /api/ventas/{id}            una venta con sus lineas
 *   POST   /api/ventas                 registra la venta completa
 *   PATCH  /api/ventas/{id}/anular     anula el comprobante
 *
 * Un PUT o un DELETE sobre /api/ventas/{id} responden 405, que es la
 * respuesta correcta: esas operaciones no existen para un comprobante.
 */
@Tag(name = "Ventas", description = "Boletas y facturas. No se editan ni se borran: se anulan.")
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
    @Operation(summary = "Lista todas las ventas")
    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(aRespuestas(ventaService.listar()));
    }

    /** GET /api/ventas/paginado?page=0&size=10&sort=fechaVenta,desc&buscar= */
    @Operation(summary = "Lista paginada de ventas, con busqueda opcional",
            description = "page empieza en 0, size maximo 50. buscar filtra por comprobante o cliente.")
    @ApiResponse(responseCode = "200", description = "Una pagina con content, totalElements y totalPages")
    @GetMapping("/paginado")
    public ResponseEntity<PaginaResponse<VentaResponse>> listarPaginado(
            @Parameter(description = "Comprobante o nombre del cliente (opcional)")
            @RequestParam(required = false) String buscar,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(PaginaResponse.de(
                ventaService.listarPaginado(buscar, pageable), this::aRespuesta));
    }

    /** GET /api/ventas/{id} */
    @Operation(summary = "Busca una venta con sus lineas")
    @ApiResponse(responseCode = "200", description = "Venta encontrada")
    @ApiResponse(responseCode = "404", description = "No existe la venta",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
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
     *
     * Si una linea falla, la transaccion hace rollback completo: no queda ni
     * la cabecera ni el stock descontado (ver VentaServiceImpl.registrar).
     */
    @Operation(summary = "Registra una venta completa (cabecera y lineas)",
            description = "Todo va en una transaccion: si una linea falla, no se guarda nada.")
    @ApiResponse(responseCode = "201", description = "Venta registrada, con Location")
    @ApiResponse(responseCode = "400", description = "Datos invalidos o rechazo de un trigger (stock insuficiente)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "404", description = "Cliente, vendedor o producto inexistente",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
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
    @Operation(summary = "Anula un comprobante (estado = ANULADA)")
    @PatchMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anular(@PathVariable Integer id) {
        return ResponseEntity.ok(aRespuesta(ventaService.anular(id)));
    }

    /** GET /api/ventas/cliente/{idCliente} - historial de compras de un cliente. */
    @Operation(summary = "Lista los registros de un cliente")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaResponse>> porCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(aRespuestas(ventaService.listarPorCliente(idCliente)));
    }

    /** GET /api/ventas/vendedor/{idUsuario} - ventas de un vendedor. */
    @Operation(summary = "Ventas de un vendedor")
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
