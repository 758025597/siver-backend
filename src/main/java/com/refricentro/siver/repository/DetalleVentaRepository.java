package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.DetalleVenta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"producto"})
    List<DetalleVenta> findByVenta_IdVenta(Integer idVenta);

    List<DetalleVenta> findByProducto_IdProducto(Integer idProducto);

    /**
     * Trae DetalleVenta junto con sus relaciones en un solo SELECT con JOIN.
     *
     * Sin esto, al armar la respuesta fuera de la transaccion Hibernate
     * lanzaria LazyInitializationException, porque las relaciones son LAZY.
     */
    @Override
    @EntityGraph(attributePaths = {"producto", "venta"})
    List<DetalleVenta> findAll();

    @Override
    @EntityGraph(attributePaths = {"producto", "venta"})
    Optional<DetalleVenta> findById(Integer id);
}
