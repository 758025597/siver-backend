package com.refricentro.siver.service.impl;

import com.refricentro.siver.dto.MovimientoInventarioRequest;
import com.refricentro.siver.model.Producto;
import com.refricentro.siver.security.UsuarioActual;
import com.refricentro.siver.service.NotificacionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.model.MovimientoInventario;
import com.refricentro.siver.model.enums.TipoMovimiento;
import com.refricentro.siver.repository.MovimientoInventarioRepository;
import com.refricentro.siver.service.MovimientoInventarioService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UsuarioActual usuarioActual;
    private final NotificacionService notificacionService;
    private final EntityManager em;

    public MovimientoInventarioServiceImpl(MovimientoInventarioRepository repositorio,
                                           UsuarioActual usuarioActual,
                                           NotificacionService notificacionService,
                                           EntityManager em) {
        this.repositorio = repositorio;
        this.usuarioActual = usuarioActual;
        this.notificacionService = notificacionService;
        this.em = em;
    }

    /**
     * Registra un movimiento manual y actualiza el stock del producto.
     *
     * El producto se relee con bloqueo (SELECT ... FOR UPDATE): si justo en
     * ese momento entra una venta del mismo producto, espera a que termine
     * este ajuste. Asi el stock_anterior del kardex siempre es el real.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventario registrarManual(MovimientoInventarioRequest request) {
        Producto producto = em.find(Producto.class, request.getIdProducto());
        if (producto == null) {
            throw new RecursoNoEncontradoException("Producto", request.getIdProducto());
        }
        em.refresh(producto, LockModeType.PESSIMISTIC_WRITE);
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalArgumentException("El producto esta inactivo: no se puede mover su stock.");
        }

        int anterior = producto.getStock();
        int cantidad = request.getCantidad();
        int nuevo = switch (request.getTipoMovimiento()) {
            case ENTRADA -> anterior + cantidad;
            case SALIDA -> anterior - cantidad;
            case AJUSTE -> cantidad;
        };

        if (request.getTipoMovimiento() != TipoMovimiento.AJUSTE && cantidad == 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (nuevo < 0) {
            throw new IllegalArgumentException(
                    "Stock insuficiente: hay " + anterior + " y se quieren sacar " + cantidad + ".");
        }
        if (nuevo == anterior) {
            throw new IllegalArgumentException(
                    "El stock contado es igual al actual (" + anterior + "): no hay nada que ajustar.");
        }

        producto.setStock(nuevo);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setUsuario(usuarioActual.obtener().orElse(null));
        mov.setTipoMovimiento(request.getTipoMovimiento());
        mov.setCantidad(Math.abs(nuevo - anterior));
        mov.setStockAnterior(anterior);
        mov.setStockNuevo(nuevo);
        mov.setMotivo(request.getMotivo().trim());
        MovimientoInventario guardado = repositorio.save(mov);

        em.flush();
        em.refresh(guardado);   // trae la fecha que puso MySQL
        notificacionService.verificarStockBajo(List.of(producto.getIdProducto()));
        return guardado;
    }

    @Override
    public List<MovimientoInventario> listar() {
        return repositorio.findAll();
    }

    @Override
    public Page<MovimientoInventario> listarPaginado(String texto, TipoMovimiento tipo, Integer idProducto,
                                                     Pageable pageable) {
        return repositorio.buscar(texto == null ? "" : texto.trim(), tipo, idProducto, pageable);
    }

    @Override
    public MovimientoInventario buscarPorId(Integer id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("MovimientoInventario", id));
    }

    @Override
    public List<MovimientoInventario> listarPorProducto(Integer idProducto) {
        return repositorio.findByProducto_IdProducto(idProducto);
    }

    @Override
    public List<MovimientoInventario> listarPorUsuario(Integer idUsuario) {
        return repositorio.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<MovimientoInventario> listarPorVenta(Integer idVenta) {
        return repositorio.findByVenta_IdVenta(idVenta);
    }
}
