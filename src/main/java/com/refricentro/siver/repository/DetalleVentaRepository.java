
package com.refricentro.siver.repository;

import com.refricentro.siver.model.DetalleVenta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository
        extends IGenericRepository<DetalleVenta, Integer> {

    @EntityGraph(attributePaths = {"producto"})
    List<DetalleVenta> findByVenta_IdVenta(Integer idVenta);

    List<DetalleVenta> findByProducto_IdProducto(Integer idProducto);

    @Override
    @EntityGraph(attributePaths = {"producto", "venta"})
    List<DetalleVenta> findAll();

    @Override
    @EntityGraph(attributePaths = {"producto", "venta"})
    Optional<DetalleVenta> findById(Integer id);
}
