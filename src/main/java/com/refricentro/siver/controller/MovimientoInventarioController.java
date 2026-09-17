package com.refricentro.siver.controller;

import com.refricentro.siver.dto.MovimientoInventarioResponse;
import com.refricentro.siver.modelos.MovimientoInventario;
import com.refricentro.siver.service.MovimientoInventarioService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta de la bitacora de inventario.
 *
 * NO hereda de ControladorGenerico, y es a proposito: esa clase trae POST,
 * PUT y DELETE, y aqui las tres estarian de mas.
 *
 * movimiento_inventario es el libro de entradas y salidas de stock. Lo
 * escriben los triggers de MySQL cuando ocurre una venta. Si la API
 * permitiera crear movimientos, se podrian registrar operaciones que nunca
 * pasaron; si permitiera borrarlos, se perderia el rastro de por que cambio
 * el stock. Y el borrado seria FISICO, porque esta tabla no tiene columna
 * `activo`: la fila desapareceria para siempre.
 *
 * Por eso solo expone GET. Es el ejemplo mas claro del proyecto de que no
 * todas las entidades necesitan las 4 operaciones del CRUD.
 */
@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioService servicio;

    public MovimientoInventarioController(MovimientoInventarioService servicio) {
        this.servicio = servicio;
    }

    private MovimientoInventarioResponse aRespuesta(MovimientoInventario m) {
        return new MovimientoInventarioResponse(
                m.getIdMovimiento(),
                m.getIdProducto(),
                m.getIdUsuario(),
                m.getIdVenta(),
                m.getTipoMovimiento(),
                m.getCantidad(),
                m.getStockAnterior(),
                m.getStockNuevo(),
                m.getMotivo(),
                m.getFechaMovimiento()
        );
    }

    private ResponseEntity<List<MovimientoInventarioResponse>> responder(
            List<MovimientoInventario> movimientos) {
        return ResponseEntity.ok(movimientos.stream().map(this::aRespuesta).toList());
    }

    /** GET /api/movimientos-inventario */
    @GetMapping
    public ResponseEntity<List<MovimientoInventarioResponse>> listar() {
        return responder(servicio.listar());
    }

    /** GET /api/movimientos-inventario/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(aRespuesta(servicio.buscarPorId(id)));
    }

    /** GET /api/movimientos-inventario/producto/{idProducto} - el kardex de un producto. */
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<MovimientoInventarioResponse>> porProducto(
            @PathVariable Integer idProducto) {
        return responder(servicio.listarPorProducto(idProducto));
    }

    /** GET /api/movimientos-inventario/usuario/{idUsuario} - quien movio el stock. */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MovimientoInventarioResponse>> porUsuario(
            @PathVariable Integer idUsuario) {
        return responder(servicio.listarPorUsuario(idUsuario));
    }

    /** GET /api/movimientos-inventario/venta/{idVenta} - que descontó una venta. */
    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<List<MovimientoInventarioResponse>> porVenta(
            @PathVariable Integer idVenta) {
        return responder(servicio.listarPorVenta(idVenta));
    }
}
