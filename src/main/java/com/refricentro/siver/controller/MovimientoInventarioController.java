package com.refricentro.siver.controller;

import com.refricentro.siver.dto.MovimientoInventarioRequest;
import com.refricentro.siver.dto.MovimientoInventarioResponse;
import com.refricentro.siver.modelos.MovimientoInventario;
import com.refricentro.siver.service.MovimientoInventarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController
        extends ControladorGenerico<
        MovimientoInventario,
        Integer,
        MovimientoInventarioRequest,
        MovimientoInventarioResponse> {

    public MovimientoInventarioController(
            MovimientoInventarioService servicio) {

        super(servicio);
    }

    @Override
    protected MovimientoInventario aEntidad(
            MovimientoInventarioRequest request) {

        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setIdProducto(request.getIdProducto());
        movimiento.setIdUsuario(request.getIdUsuario());
        movimiento.setIdVenta(request.getIdVenta());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setStockAnterior(request.getStockAnterior());
        movimiento.setStockNuevo(request.getStockNuevo());
        movimiento.setMotivo(request.getMotivo());

        return movimiento;
    }

    @Override
    protected MovimientoInventarioResponse aRespuesta(
            MovimientoInventario movimiento) {

        return new MovimientoInventarioResponse(
                movimiento.getIdMovimiento(),
                movimiento.getIdProducto(),
                movimiento.getIdUsuario(),
                movimiento.getIdVenta(),
                movimiento.getTipoMovimiento(),
                movimiento.getCantidad(),
                movimiento.getStockAnterior(),
                movimiento.getStockNuevo(),
                movimiento.getMotivo(),
                movimiento.getFechaMovimiento()
        );
    }
}