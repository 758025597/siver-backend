package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.DetalleVenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de las lineas de una venta.
 *
 * No tiene servicio ni controlador propios: una linea no existe por si sola,
 * se crea junto con su venta. Este repositorio lo usa VentaServiceImpl.
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    List<DetalleVenta> findByIdVenta(Integer idVenta);

    List<DetalleVenta> findByIdProducto(Integer idProducto);
}
