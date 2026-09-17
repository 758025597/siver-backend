package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.MovimientoInventario;
import com.refricentro.siver.repository.MovimientoInventarioRepository;
import com.refricentro.siver.service.MovimientoInventarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoInventarioServiceImpl
        extends ServicioGenerico<MovimientoInventario, Integer>
        implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public MovimientoInventarioServiceImpl(
            MovimientoInventarioRepository movimientoInventarioRepository) {

        super(movimientoInventarioRepository);
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "MovimientoInventario";
    }

    @Override
    protected void copiarDatos(
            MovimientoInventario origen,
            MovimientoInventario destino) {

        destino.setIdProducto(origen.getIdProducto());
        destino.setIdUsuario(origen.getIdUsuario());
        destino.setIdVenta(origen.getIdVenta());
        destino.setTipoMovimiento(origen.getTipoMovimiento());
        destino.setCantidad(origen.getCantidad());
        destino.setStockAnterior(origen.getStockAnterior());
        destino.setStockNuevo(origen.getStockNuevo());
        destino.setMotivo(origen.getMotivo());
    }

    @Override
    public List<MovimientoInventario> listarPorProducto(Integer idProducto) {
        return movimientoInventarioRepository.findByIdProducto(idProducto);
    }

    @Override
    public List<MovimientoInventario> listarPorUsuario(Integer idUsuario) {
        return movimientoInventarioRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public List<MovimientoInventario> listarPorVenta(Integer idVenta) {
        return movimientoInventarioRepository.findByIdVenta(idVenta);
    }
}