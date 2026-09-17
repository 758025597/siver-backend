package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.Venta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de la cabecera de las ventas. */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    /** El comprobante es unico por tipo: puede existir BOLETA 001 y FACTURA 001. */
    Optional<Venta> findByTipoComprobanteAndNumeroComprobante(
            Venta.TipoComprobante tipoComprobante, String numeroComprobante);

    boolean existsByTipoComprobanteAndNumeroComprobante(
            Venta.TipoComprobante tipoComprobante, String numeroComprobante);

    List<Venta> findByEstado(Venta.EstadoVenta estado);

    List<Venta> findByIdCliente(Integer idCliente);

    List<Venta> findByIdUsuario(Integer idUsuario);

    /** Para el reporte de ventas del dia o del mes. */
    List<Venta> findByFechaVentaBetween(LocalDateTime desde, LocalDateTime hasta);
}
