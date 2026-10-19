
package com.refricentro.siver.repository;

import com.refricentro.siver.model.Venta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

/** Acceso a datos de la cabecera de las ventas. */
@Repository
public interface VentaRepository extends IGenericRepository<Venta, Integer> {

    /** El comprobante es único por tipo. */
    Optional<Venta> findByTipoComprobanteAndNumeroComprobante(
            Venta.TipoComprobante tipoComprobante, String numeroComprobante);

    boolean existsByTipoComprobanteAndNumeroComprobante(
            Venta.TipoComprobante tipoComprobante, String numeroComprobante);

    List<Venta> findByEstado(Venta.EstadoVenta estado);

    List<Venta> findByCliente_IdCliente(Integer idCliente);

    List<Venta> findByUsuario_IdUsuario(Integer idUsuario);

    /** Para reportes de ventas por período. */
    List<Venta> findByFechaVentaBetween(LocalDateTime desde, LocalDateTime hasta);

    @Override
    @EntityGraph(attributePaths = {"cliente", "usuario"})
    List<Venta> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "usuario"})
    Optional<Venta> findById(Integer id);
}
