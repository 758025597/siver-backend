package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.modelos.MovimientoInventario;
import com.refricentro.siver.repository.MovimientoInventarioRepository;
import com.refricentro.siver.service.MovimientoInventarioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultas sobre la bitacora de inventario.
 *
 * No hereda de ServicioGenerico ni de ServicioSoftDelete: esas clases traen
 * crear, actualizar y eliminar, y aqui ninguna de las tres debe existir.
 * Todos los metodos son de solo lectura.
 */
@Service
@Transactional(readOnly = true)
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository repositorio;

    public MovimientoInventarioServiceImpl(MovimientoInventarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<MovimientoInventario> listar() {
        return repositorio.findAll();
    }

    @Override
    public MovimientoInventario buscarPorId(Integer id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("MovimientoInventario", id));
    }

    @Override
    public List<MovimientoInventario> listarPorProducto(Integer idProducto) {
        return repositorio.findByIdProducto(idProducto);
    }

    @Override
    public List<MovimientoInventario> listarPorUsuario(Integer idUsuario) {
        return repositorio.findByIdUsuario(idUsuario);
    }

    @Override
    public List<MovimientoInventario> listarPorVenta(Integer idVenta) {
        return repositorio.findByIdVenta(idVenta);
    }
}
