package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.modelos.DetalleVenta;
import com.refricentro.siver.modelos.Venta;
import com.refricentro.siver.repository.DetalleVentaRepository;
import com.refricentro.siver.repository.VentaRepository;
import com.refricentro.siver.service.VentaService;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de las ventas.
 *
 * No hereda de ServicioGenerico ni de ServicioSoftDelete: esas clases traen
 * actualizar() y eliminar(), y una venta no se edita ni se borra.
 *
 * Buena parte del trabajo lo hacen los triggers de MySQL, no este codigo:
 *
 *   trg_venta_before_insert
 *       solo permite FACTURA si el cliente tiene RUC
 *
 *   trg_detalle_venta_before_insert
 *       rechaza productos inactivos, verifica que haya stock suficiente
 *       y calcula subtotal = (cantidad * precio_unitario) - descuento
 *
 *   trg_detalle_venta_after_insert
 *       descuenta el stock del producto, escribe la fila en
 *       movimiento_inventario y RECALCULA subtotal, igv y total de la
 *       cabecera con IGV del 18%
 */
@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final EntityManager em;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            DetalleVentaRepository detalleVentaRepository,
                            EntityManager em) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.em = em;
    }

    /**
     * Registra la venta completa en una sola transaccion.
     *
     * El flush() y el refresh() del final NO son adorno:
     *
     * Hibernate junta los INSERT y los manda al final de la transaccion. Sin
     * el flush(), los triggers todavia no habrian corrido. Y aunque corran,
     * los totales los calcula MySQL por dentro: el objeto Venta que tenemos
     * en memoria sigue con subtotal, igv y total en 0.00.
     *
     * El refresh() vuelve a leer la fila desde la base, ya con los montos
     * calculados. Sin el, la API devolveria una venta con total 0.00 aunque
     * en la base estuviera correcta.
     */
    @Override
    @Transactional
    public Venta registrar(Venta venta, List<DetalleVenta> detalles) {
        Venta guardada = ventaRepository.save(venta);

        for (DetalleVenta detalle : detalles) {
            detalle.setIdVenta(guardada.getIdVenta());
            // El subtotal lo calcula el trigger: aqui se manda en cero.
            detalleVentaRepository.save(detalle);
        }

        em.flush();             // ejecuta los INSERT y dispara los triggers
        em.refresh(guardada);   // relee los totales que calculo MySQL
        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Venta buscarPorId(Integer id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> listarDetalles(Integer idVenta) {
        buscarPorId(idVenta); // valida que la venta exista, si no da 404
        return detalleVentaRepository.findByIdVenta(idVenta);
    }

    /**
     * ANULAR es el "borrado" de una venta: la fila se queda, cambia el estado.
     *
     * Ojo con una cosa: anular NO devuelve el stock. La base no tiene ningun
     * trigger que lo haga, asi que si se anula una venta el inventario queda
     * descontado. Si el negocio necesita devolver el stock, hay que registrar
     * un movimiento de ENTRADA aparte.
     */
    @Override
    @Transactional
    public Venta anular(Integer id) {
        Venta venta = buscarPorId(id);
        venta.setEstado(Venta.EstadoVenta.ANULADA);
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarPorCliente(Integer idCliente) {
        return ventaRepository.findByIdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarPorVendedor(Integer idUsuario) {
        return ventaRepository.findByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarPorEstado(Venta.EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }
}
