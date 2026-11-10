package com.refricentro.siver.controller;

import com.refricentro.siver.dto.MovimientoInventarioResponse;
import com.refricentro.siver.dto.PaginaResponse;
import com.refricentro.siver.exception.ErrorRespuesta;
import com.refricentro.siver.model.MovimientoInventario;
import com.refricentro.siver.model.enums.TipoMovimiento;
import com.refricentro.siver.service.MovimientoInventarioService;
import com.refricentro.siver.util.MapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@Tag(name = "Movimientos de inventario", description = "Kardex de solo lectura: lo escriben los triggers de MySQL.")
@RestController
@RequestMapping("/api/movimientos-inventario")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    private final MovimientoInventarioService servicio;

    @Qualifier("movimientoMapper")
    private final ModelMapper mapper;

    private MovimientoInventarioResponse aRespuesta(MovimientoInventario m) {
        return mapper.map(m, MovimientoInventarioResponse.class);
    }

    private ResponseEntity<List<MovimientoInventarioResponse>> responder(
            List<MovimientoInventario> movimientos) {
        return ResponseEntity.ok(
                MapperUtil.mapList(mapper, movimientos, MovimientoInventarioResponse.class));
    }

    /** GET /api/movimientos-inventario */
    @Operation(summary = "Lista todo el kardex")
    @GetMapping
    public ResponseEntity<List<MovimientoInventarioResponse>> listar() {
        return responder(servicio.listar());
    }

    /** GET /api/movimientos-inventario/paginado?page=0&size=10&buscar=&tipo=SALIDA&idProducto= */
    @Operation(summary = "Kardex paginado, con filtro por texto y por tipo",
            description = "page empieza en 0, size maximo 50. tipo: ENTRADA, SALIDA o AJUSTE.")
    @ApiResponse(responseCode = "200", description = "Una pagina con content, totalElements y totalPages")
    @GetMapping("/paginado")
    public ResponseEntity<PaginaResponse<MovimientoInventarioResponse>> listarPaginado(
            @Parameter(description = "Codigo o nombre del producto, o motivo (opcional)")
            @RequestParam(required = false) String buscar,
            @Parameter(description = "Tipo de movimiento (opcional)")
            @RequestParam(required = false) TipoMovimiento tipo,
            @Parameter(description = "Solo el kardex de este producto (opcional)")
            @RequestParam(required = false) Integer idProducto,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(PaginaResponse.de(
                servicio.listarPaginado(buscar, tipo, idProducto, pageable), this::aRespuesta));
    }

    /** GET /api/movimientos-inventario/{id} */
    @Operation(summary = "Busca un movimiento por su id")
    @ApiResponse(responseCode = "200", description = "Movimiento encontrado")
    @ApiResponse(responseCode = "404", description = "No existe el movimiento",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(aRespuesta(servicio.buscarPorId(id)));
    }

    /** GET /api/movimientos-inventario/producto/{idProducto} - el kardex de un producto. */
    @Operation(summary = "Kardex de un producto")
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<MovimientoInventarioResponse>> porProducto(
            @PathVariable Integer idProducto) {
        return responder(servicio.listarPorProducto(idProducto));
    }

    /** GET /api/movimientos-inventario/usuario/{idUsuario} - quien movio el stock. */
    @Operation(summary = "Movimientos hechos por un usuario")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MovimientoInventarioResponse>> porUsuario(
            @PathVariable Integer idUsuario) {
        return responder(servicio.listarPorUsuario(idUsuario));
    }

    /** GET /api/movimientos-inventario/venta/{idVenta} - que descontó una venta. */
    @Operation(summary = "Movimientos que genero una venta")
    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<List<MovimientoInventarioResponse>> porVenta(
            @PathVariable Integer idVenta) {
        return responder(servicio.listarPorVenta(idVenta));
    }
}
